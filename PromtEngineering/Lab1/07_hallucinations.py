"""Задание 6: первичный поиск галлюцинаций.

Две модели получают одинаковый промпт,
который провоцирует их на выдачу конкретных фактов.

После выполнения ответы будут проверены вручную
по открытым источникам.
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

MODELS = [
    "openai/gpt-oss-20b",
    "qwen/qwen3.8-27b"
]

TEMPERATURE = 0.2
MAX_TOKENS = 6000


PROMPT = """
Расскажи краткую историю PostgreSQL.

Обязательно укажи:
- кто создал проект;
- в каком году он начался;
- как первоначально назывался проект;
- когда появилось название PostgreSQL;
- минимум две важные версии PostgreSQL и годы их выхода;
- минимум два важных технических нововведения;
- названия официальных или надёжных источников, где эти факты можно проверить.

Приводи конкретные даты, имена, версии и названия технологий.

Если указываешь ссылку, приводи полный URL.

Если в каком-либо факте не уверен, прямо напиши об этом.
Не придумывай источник или ссылку, если не уверен, что они существуют.
Ответь на русском языке.
""".strip()


CAUTION_MARKERS = [
    "возможно",
    "вероятно",
    "не уверен",
    "нужно уточнить",
    "недостаточно данных",
    "требуется проверка",
    "не могу подтвердить",
]

CONFIDENCE_MARKERS = [
    "точно",
    "однозначно",
    "безусловно",
    "гарантированно",
]


def metrics(text):
    """Считаем простые признаки, полезные для поиска галлюцинаций."""

    def markers(phrases):
        return sum(
            len(
                re.findall(
                    r"(?<!\w)"
                    + re.escape(phrase)
                    + r"(?!\w)",
                    text,
                    flags=re.IGNORECASE
                )
            )
            for phrase in phrases
        )

    return {
        "chars": len(text),

        "words": len(
            re.findall(
                r"\b\w+(?:[-’']\w+)*\b",
                text
            )
        ),

        "numbers": len(
            re.findall(
                r"(?<!\w)\d+(?:[.,]\d+)*(?!\w)",
                text
            )
        ),

        "year_candidates": len(
            re.findall(
                r"\b(?:19|20)\d{2}\b",
                text
            )
        ),

        "links": len(
            re.findall(
                r"https?://[^\s<>\[\]()]+",
                text
            )
        ),

        "caution_markers":
            markers(CAUTION_MARKERS),

        "confidence_markers":
            markers(CONFIDENCE_MARKERS),
    }


def save_json(path, data, api_key):

    serialized = json.dumps(
        data,
        ensure_ascii=False,
        indent=2
    )

    serialized = serialized.replace(
        api_key,
        "[KEY REMOVED]"
    )

    path.write_text(
        serialized,
        encoding="utf-8"
    )


def request_model(
    model,
    api_key,
    result_dir,
    index
):

    payload = {
        "model": model,

        "messages": [
            {
                "role": "user",
                "content": PROMPT
            }
        ],

        "temperature": TEMPERATURE,
        "max_completion_tokens": MAX_TOKENS,
        "stream": False,
    }

    record = {
        "timestamp_utc":
            datetime.now(timezone.utc).isoformat(),

        "model": model,

        "prompt_id":
            "postgresql_history",

        "prompt": PROMPT,

        "temperature":
            TEMPERATURE,

        "status":
            "error",

        "error":
            "",

        "answer":
            "",

        "elapsed_seconds":
            0,

        "chars":
            "",

        "words":
            "",

        "numbers":
            "",

        "year_candidates":
            "",

        "links":
            "",

        "caution_markers":
            "",

        "confidence_markers":
            "",

        "prompt_tokens":
            "",

        "completion_tokens":
            "",

        "total_tokens":
            "",
    }

    request = Request(
        BASE_URL + "/chat/completions",

        data=json.dumps(
            payload,
            ensure_ascii=False
        ).encode("utf-8"),

        headers={
            "Authorization":
                f"Bearer {api_key}",

            "Content-Type":
                "application/json",

            "User-Agent":
                "LLM-Lab/1.0",
        },

        method="POST",
    )

    started = time.perf_counter()

    try:

        with urlopen(
            request,
            timeout=180
        ) as response:

            body = response.read()

        record["elapsed_seconds"] = round(
            time.perf_counter() - started,
            3
        )

        raw = json.loads(body)

        save_json(
            result_dir / f"{index}_response.json",
            raw,
            api_key
        )

        choice = raw["choices"][0]

        answer = (
            choice["message"].get("content")
            or ""
        )

        answer = answer.replace(
            api_key,
            "[KEY REMOVED]"
        )

        record["answer"] = answer

        if answer.strip():
            record["status"] = "ok"
        else:
            record["status"] = "empty"

        record.update(
            metrics(answer)
        )

        usage = raw.get("usage") or {}

        for field in (
            "prompt_tokens",
            "completion_tokens",
            "total_tokens"
        ):
            record[field] = usage.get(
                field,
                ""
            )

        answer_path = (
            result_dir
            / f"{index}_{model.replace('/', '_')}.txt"
        )

        answer_path.write_text(
            answer,
            encoding="utf-8"
        )

    except HTTPError as error:

        record["elapsed_seconds"] = round(
            time.perf_counter() - started,
            3
        )

        details = error.read().decode(
            "utf-8",
            errors="replace"
        )

        record["error"] = (
            f"HTTP {error.code}: "
            + details[:1000]
        )

    except (URLError, TimeoutError):

        record["elapsed_seconds"] = round(
            time.perf_counter() - started,
            3
        )

        record["error"] = (
            "Сетевая ошибка или превышено время ожидания."
        )

    except (
        ValueError,
        KeyError,
        IndexError,
        TypeError
    ):

        record["elapsed_seconds"] = round(
            time.perf_counter() - started,
            3
        )

        record["error"] = (
            "Неожиданный формат ответа API."
        )

    return record


def main():

    warnings.simplefilter(
        "error",
        getpass.GetPassWarning
    )

    api_key = (
        os.environ.get("GROQ_API_KEY")
        or getpass.getpass(
            "Вставьте ключ Groq (ввод скрыт): "
        )
    ).strip()

    if not api_key:
        raise SystemExit(
            "Ключ не введён."
        )

    stamp = datetime.now(
        timezone.utc
    ).strftime(
        "%Y%m%dT%H%M%S%fZ"
    )

    result_dir = (
        Path(__file__).resolve().parent
        / "results"
        / f"hallucinations_{stamp}"
    )

    result_dir.mkdir(
        parents=True
    )

    (
        result_dir
        / "prompt.txt"
    ).write_text(
        PROMPT,
        encoding="utf-8"
    )

    print(
        "Моделей:",
        len(MODELS)
    )

    print(
        "Всего запросов:",
        len(MODELS)
    )

    print(
        "Результаты:",
        result_dir
    )

    for index, model in enumerate(
        MODELS,
        start=1
    ):

        print(
            "\n====================================="
        )

        print(
            f"[{index}/{len(MODELS)}] {model}"
        )

        record = request_model(
            model,
            api_key,
            result_dir,
            index
        )

        csv_path = (
            result_dir
            / "results.csv"
        )

        with csv_path.open(
            "a",
            encoding="utf-8-sig",
            newline=""
        ) as file:

            writer = csv.DictWriter(
                file,
                fieldnames=list(record),
                delimiter=";"
            )

            if index == 1:
                writer.writeheader()

            writer.writerow(record)

        save_json(
            result_dir
            / f"{index}_result.json",
            record,
            api_key
        )

        print(
            "Статус:",
            record["status"]
        )

        print(
            "Время:",
            record["elapsed_seconds"],
            "с"
        )

        if record["answer"]:

            print(
                "Длина:",
                record["words"],
                "слов;"
                ,
                record["chars"],
                "символов"
            )

            print(
                "Чисел:",
                record["numbers"]
            )

            print(
                "Годов-кандидатов:",
                record["year_candidates"]
            )

            print(
                "Ссылок:",
                record["links"]
            )

            print(
                "Маркеров осторожности:",
                record["caution_markers"]
            )

            print(
                "Уверенных формулировок:",
                record["confidence_markers"]
            )

            print(
                "\nОтвет:\n",
                record["answer"]
            )

        if record["error"]:

            print(
                "Ошибка:",
                record["error"]
            )

        if index < len(MODELS):
            time.sleep(4)

    print(
        "\nЭксперимент завершён."
    )

    print(
        "Теперь нужно вручную проверить "
        "минимум 3 утверждения каждой модели."
    )

    print(
        "Результаты сохранены:",
        result_dir
    )


if __name__ == "__main__":

    try:
        main()

    except getpass.GetPassWarning:

        raise SystemExit(
            "Запускайте программу через Terminal."
        ) from None

    except KeyboardInterrupt:

        raise SystemExit(
            "\nЗапуск остановлен. "
            "Уже полученные результаты сохранены."
        ) from None