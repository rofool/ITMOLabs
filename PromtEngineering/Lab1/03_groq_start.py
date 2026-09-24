"""Задания 1–2: одинаковый первый запрос к GPT-OSS и Qwen через Groq.

Запуск: 03_groq_start.py
Один запуск = две попытки генерации; автоматических повторов нет.
Используются только стандартные библиотеки Python 3.
"""

import csv
import getpass
import json
import os
import re
import time
import warnings
from datetime import datetime, timezone
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

BASE_URL = "https://api.groq.com/openai/v1"
MODELS = ["openai/gpt-oss-20b", "qwen/qwen3.8-27b"]
PROMPT = (
    "Объясни простыми словами, что такое индекс в реляционной базе данных. "
    "Для чего он нужен? Приведи один понятный пример, укажи преимущество "
    "и недостаток использования индекса. Ответь на русском языке, не более 150 слов."
)
TEMPERATURE = 0.2
MAX_TOKENS = 4096
CAUTION = ["возможно", "вероятно", "нужно уточнить", "недостаточно данных", "требуется проверка"]
CONFIDENCE = ["точно", "однозначно", "безусловно", "гарантированно"]


def metrics(text):
    """Считаем вхождения, а не уникальные значения. Это индикаторы, не оценки."""
    def markers(phrases):
        return sum(len(re.findall(r"(?<!\w)" + re.escape(p) + r"(?!\w)", text,
                                  flags=re.IGNORECASE)) for p in phrases)
    return {
        "chars": len(text),  # Включая пробелы и переводы строк.
        "words": len(re.findall(r"\b\w+(?:[-’']\w+)*\b", text)),
        "numbers": len(re.findall(r"(?<!\w)\d+(?:[.,]\d+)*(?!\w)", text)),
        # Четырёхзначные числа 1900–2099: кандидаты на годы, не проверенные даты.
        "year_candidates": len(re.findall(r"\b(?:19|20)\d{2}\b", text)),
        "links": len(re.findall(r"https?://[^\s<>\[\]()]+", text)),
        "caution_markers": markers(CAUTION),
        "confidence_markers": markers(CONFIDENCE),
    }


def save_json(path, data, api_key):
    # Дополнительная защита: ключ не должен попасть в сохранённый ответ сервиса.
    serialized = json.dumps(data, ensure_ascii=False, indent=2)
    path.write_text(serialized.replace(api_key, "[KEY REMOVED]"), encoding="utf-8")


def request_model(model, api_key, result_dir, index):
    payload = {
        "model": model,
        "messages": [{"role": "user", "content": PROMPT}],
        "temperature": TEMPERATURE,
        "max_completion_tokens": MAX_TOKENS,
        "stream": False,
    }
    record = {
        "timestamp_utc": datetime.now(timezone.utc).isoformat(),
        "model": model, "returned_model": "", "provider": "Groq", "base_url": BASE_URL,
        "prompt_id": "explain_index", "prompt": PROMPT,
        "temperature": TEMPERATURE, "max_completion_tokens": MAX_TOKENS,
        "reasoning_setting": "provider default",
        "status": "error", "error": "", "answer": "", "elapsed_seconds": 0,
        "finish_reason": "", "prompt_tokens": "", "completion_tokens": "",
        "total_tokens": "", **{key: "" for key in metrics("")},
    }
    # Сохраняем точный запрос без заголовка, содержащего ключ.
    save_json(result_dir / f"{index}_request.json", payload, api_key)
    request = Request(
        BASE_URL + "/chat/completions",
        data=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
        headers={"Authorization": f"Bearer {api_key}", "Content-Type": "application/json", "User-Agent": "LLM-Lab/1.0"},
        method="POST",
    )
    started = time.perf_counter()
    try:
        with urlopen(request, timeout=180) as response:
            body = response.read()
        # Полное время запроса: сеть, ожидание и генерация; не чистая скорость модели.
        record["elapsed_seconds"] = round(time.perf_counter() - started, 3)
        raw = json.loads(body)
        save_json(result_dir / f"{index}_response.json", raw, api_key)
        if "error" in raw:
            record["error"] = json.dumps(raw["error"], ensure_ascii=False).replace(api_key, "[KEY REMOVED]")[:1000]
            return record
        choice = raw["choices"][0]
        answer = choice["message"].get("content") or ""
        if not isinstance(answer, str):
            raise ValueError("Unexpected content type")
        answer = answer.replace(api_key, "[KEY REMOVED]")
        finish = choice.get("finish_reason", "")
        record.update({
            "returned_model": raw.get("model", ""), "provider": raw.get("provider", "Groq"),
            "answer": answer, "finish_reason": finish,
            "status": ("empty" if not answer.strip() else "ok" if finish == "stop" else "incomplete"),
            **metrics(answer),
        })
        usage = raw.get("usage") or {}
        for field in ("prompt_tokens", "completion_tokens", "total_tokens"):
            record[field] = usage.get(field, "")
        (result_dir / f"{index}_answer.txt").write_text(answer, encoding="utf-8")
    except HTTPError as error:
        record["elapsed_seconds"] = round(time.perf_counter() - started, 3)
        record["error"] = f"HTTP {error.code}"
        # Ошибки провайдера полезны для диагностики, но ключ предварительно удаляем.
        details = error.read().decode("utf-8", errors="replace").replace(api_key, "[KEY REMOVED]")
        (result_dir / f"{index}_error.txt").write_text(details, encoding="utf-8")
    except (URLError, TimeoutError):
        record["elapsed_seconds"] = round(time.perf_counter() - started, 3)
        record["error"] = "Сетевая ошибка или превышено время ожидания."
    except (ValueError, KeyError, IndexError, TypeError):
        record["elapsed_seconds"] = round(time.perf_counter() - started, 3)
        record["error"] = "Неожиданный формат ответа API."
    return record


def save_catalog(api_key, result_dir):
    """Список моделей Groq нужен для отчёта; этот запрос не генерирует текст."""
    request = Request(BASE_URL + "/models", headers={
        "Authorization": f"Bearer {api_key}",
        "Accept": "application/json", "User-Agent": "LLM-Lab/1.0",
    })
    try:
        with urlopen(request, timeout=60) as response:
            catalog = json.load(response)
        ids = sorted(model["id"] for model in catalog["data"])
    except HTTPError as error:
        details = error.read().decode("utf-8", errors="replace").replace(api_key, "[KEY REMOVED]")
        (result_dir / "catalog_error.txt").write_text(details, encoding="utf-8")
        print(f"Каталог не получен: HTTP {error.code}.")
        print("Подробности сохранены в catalog_error.txt.")
        if error.code == 401:
            print("Проверьте, что ввели новый ключ Groq, а не OpenRouter.")
        return False
    except (URLError, TimeoutError, ValueError, KeyError, TypeError):
        print("Не удалось получить каталог: ошибка сети или формата ответа.")
        return False
    save_json(result_dir / "available_models.json", {
        "retrieved_at_utc": datetime.now(timezone.utc).isoformat(),
        "base_url": BASE_URL, **catalog,
    }, api_key)
    (result_dir / "available_models.txt").write_text("\n".join(ids), encoding="utf-8")
    print(f"Ключ принят. Моделей в каталоге Groq: {len(ids)}")
    print("\n".join(ids))
    missing = set(MODELS) - set(ids)
    if missing:
        print("В каталоге отсутствуют выбранные модели:", ", ".join(sorted(missing)))
        print("Генерация не запущена. Сначала уточним выбор моделей.")
        return False
    return True


def main():
    warnings.simplefilter("error", getpass.GetPassWarning)
    api_key = (os.environ.get("GROQ_API_KEY") or getpass.getpass(
        "Вставьте НОВЫЙ ключ Groq (ввод скрыт): "
    )).strip()
    if not api_key:
        raise SystemExit("Ключ не введён.")
    if api_key.startswith("sk-or-"):
        raise SystemExit("Это ключ OpenRouter. Запустите заново и введите ключ Groq.")
    stamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S%fZ")
    result_dir = Path(__file__).resolve().parent / "results" / f"groq_first_requests_{stamp}"
    result_dir.mkdir(parents=True)
    if not save_catalog(api_key, result_dir):
        return
    print("Промпт:", PROMPT)
    print("Результаты:", result_dir)
    successes = 0
    for index, model in enumerate(MODELS, start=1):
        print(f"\n[{index}/{len(MODELS)}] Запрос к {model}...", flush=True)
        record = request_model(model, api_key, result_dir, index)
        # Каждая строка записывается сразу: сбой следующего запроса не потеряет ответ.
        with (result_dir / "results.csv").open("a", encoding="utf-8-sig", newline="") as file:
            writer = csv.DictWriter(file, fieldnames=list(record), delimiter=";")
            if index == 1:
                writer.writeheader()
            writer.writerow(record)
        save_json(result_dir / f"{index}_result.json", record, api_key)
        print(f"Статус: {record['status']}; время: {record['elapsed_seconds']} с")
        successes += record["status"] == "ok"
        if record["answer"]:
            print(record["answer"])
            print(f"Длина: {record['chars']} символов; {record['words']} слов.")
        if record["error"]:
            print("Ошибка:", record["error"])
        if record["status"] in ("empty", "incomplete"):
            print("Ответ пустой или не завершён нормально. Не оцениваем его как полный.")
        if index < len(MODELS):
            time.sleep(4)
    print(f"\nПолных ответов: {successes}/{len(MODELS)}. Результаты: {result_dir}")


if __name__ == "__main__":
    try:
        main()
    except getpass.GetPassWarning:
        raise SystemExit("Запускайте во вкладке Terminal: здесь нельзя скрыть ввод.") from None
    except KeyboardInterrupt:
        raise SystemExit("\nЗапуск отменён. Уже записанные результаты сохранены.") from None
