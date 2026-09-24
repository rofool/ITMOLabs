"""Задание 4: ответы на вопросы по тексту.

Используется тот же исходный текст, что и в задании 3.

Три типа вопросов:
1. Ответ прямо есть в тексте.
2. Нужно понять смысл текста.
3. В тексте недостаточно информации.

3 вопроса × 2 модели = 6 запросов.
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
MAX_TOKENS = 1000

SOURCE_TEXT = """
Индексы являются одним из основных механизмов оптимизации работы
реляционных баз данных. Без индекса системе управления базами данных
часто приходится последовательно просматривать строки таблицы,
чтобы найти записи, удовлетворяющие условию запроса. Такой способ
называется полным сканированием таблицы. Для небольшой таблицы
это может быть приемлемо, однако при наличии миллионов строк
выполнение запроса может занимать значительно больше времени.

Индекс представляет собой дополнительную структуру данных,
которая хранит значения одного или нескольких столбцов таблицы
и информацию, позволяющую быстро определить расположение
соответствующих строк. По назначению индекс похож на алфавитный
указатель в книге: вместо последовательного просмотра всех страниц
можно обратиться к указателю и определить, где находится нужная
информация.

Одной из наиболее распространённых структур для реализации индексов
является B-дерево и его разновидности. Такая структура позволяет
эффективно выполнять поиск, вставку и удаление элементов. Индексы
особенно полезны для запросов, в которых используются условия WHERE,
операции JOIN, сортировка ORDER BY и некоторые виды группировки.

Например, существует таблица users, содержащая несколько миллионов
пользователей. Если приложение регулярно ищет пользователя
по адресу электронной почты, запрос вида
SELECT * FROM users WHERE email = 'user@example.com'
без подходящего индекса может потребовать просмотра большого
количества строк. Если создать индекс по столбцу email,
система управления базами данных получает возможность значительно
быстрее найти требуемую запись.

Однако наличие индекса не означает, что любой запрос автоматически
станет быстрее. Система управления базами данных использует
оптимизатор запросов, который анализирует возможные способы
выполнения операции. Оптимизатор может решить, что использование
индекса выгодно, либо выбрать полное сканирование таблицы.
Например, если запрос возвращает большую часть строк таблицы,
использование индекса иногда оказывается менее эффективным.

Индексы также требуют дополнительных ресурсов. Они занимают место
на диске и должны обновляться при изменении данных. Когда в таблицу
добавляется новая строка, удаляется существующая запись или изменяется
индексируемое значение, СУБД должна изменить не только саму таблицу,
но и соответствующие индексы. Поэтому большое количество индексов
может ускорять чтение данных, одновременно замедляя операции INSERT,
UPDATE и DELETE.

По этой причине индексы следует создавать осознанно. Обычно имеет
смысл индексировать столбцы, которые часто участвуют в поиске,
соединениях таблиц и сортировке. При этом создание индекса для каждого
столбца таблицы редко является хорошим решением. Необходимо учитывать
характер запросов, размер таблицы, частоту чтения и изменения данных.

Кроме обычных индексов по одному столбцу существуют составные индексы,
включающие несколько столбцов. Порядок столбцов в таком индексе имеет
важное значение. Индекс по полям last_name и first_name может быть
полезен для одних запросов и практически бесполезен для других.
Поэтому проектирование составных индексов требует понимания того,
какие запросы выполняются приложением наиболее часто.

Для анализа эффективности индексов разработчики используют планы
выполнения запросов. СУБД может показать, каким способом она собирается
получать данные: использовать индекс, выполнить последовательное
сканирование таблицы или применить другие операции. Анализ плана
позволяет понять причины медленной работы запроса и определить,
действительно ли создание нового индекса улучшит ситуацию.

Таким образом, индекс является инструментом оптимизации доступа
к данным, а не универсальным способом ускорения базы данных.
Грамотно выбранные индексы способны существенно повысить скорость
чтения данных, однако их чрезмерное количество увеличивает расход
дискового пространства и стоимость операций изменения данных.
Эффективная работа с индексами требует поиска баланса между скоростью
чтения, скоростью записи и использованием ресурсов.
""".strip()

QUESTIONS = [
    (
        "direct",
        "Какие операции изменения данных могут замедляться "
        "из-за необходимости обновлять индексы?"
    ),
    (
        "understanding",
        "Почему создание большого количества индексов может "
        "одновременно улучшить и ухудшить производительность базы данных?"
    ),
    (
        "insufficient",
        "Какой конкретный процент ускорения запроса гарантирует "
        "создание индекса по столбцу email?"
    ),
]


def metrics(text):
    return {
        "chars": len(text),
        "words": len(
            re.findall(r"\b\w+(?:[-’']\w+)*\b", text)
        ),
        "numbers": len(
            re.findall(r"(?<!\w)\d+(?:[.,]\d+)*(?!\w)", text)
        ),
        "caution_markers": sum(
            text.lower().count(marker)
            for marker in [
                "недостаточно информации",
                "недостаточно данных",
                "нельзя определить",
                "невозможно определить",
                "в тексте не указано",
                "не указано",
            ]
        ),
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
        question_id,
        question,
        api_key,
        result_dir,
        index
):
    full_prompt = (
        "Ответь на вопрос только на основе приведённого текста. "
        "Если в тексте недостаточно информации для корректного ответа, "
        "прямо скажи об этом и не придумывай факты.\n\n"
        f"ВОПРОС:\n{question}\n\n"
        f"ИСХОДНЫЙ ТЕКСТ:\n{SOURCE_TEXT}"
    )

    payload = {
        "model": model,
        "messages": [
            {
                "role": "user",
                "content": full_prompt
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
        "question_id": question_id,
        "question": question,

        "temperature": TEMPERATURE,

        "status": "error",
        "error": "",

        "answer": "",

        "elapsed_seconds": 0,

        "chars": "",
        "words": "",
        "numbers": "",
        "caution_markers": "",

        "prompt_tokens": "",
        "completion_tokens": "",
        "total_tokens": "",
    }

    request = Request(
        BASE_URL + "/chat/completions",

        data=json.dumps(
            payload,
            ensure_ascii=False
        ).encode("utf-8"),

        headers={
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json",
            "User-Agent": "LLM-Lab/1.0",
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
                / f"{index}_{question_id}_{model.replace('/', '_')}.txt"
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
            / f"questions_{stamp}"
    )

    result_dir.mkdir(
        parents=True
    )

    (
            result_dir
            / "source_text.txt"
    ).write_text(
        SOURCE_TEXT,
        encoding="utf-8"
    )

    with (
            result_dir
            / "questions.txt"
    ).open(
        "w",
        encoding="utf-8"
    ) as file:

        for question_id, question in QUESTIONS:
            file.write(
                f"{question_id}\n"
                f"{question}\n\n"
            )

    print(
        "Моделей:",
        len(MODELS)
    )

    print(
        "Вопросов:",
        len(QUESTIONS)
    )

    print(
        "Всего запросов:",
        len(MODELS) * len(QUESTIONS)
    )

    print(
        "Результаты:",
        result_dir
    )

    index = 0

    for question_id, question in QUESTIONS:

        print(
            "\n====================================="
        )

        print(
            "Тип вопроса:",
            question_id
        )

        print(
            "Вопрос:",
            question
        )

        for model in MODELS:

            index += 1

            print(
                f"\n[{index}/"
                f"{len(MODELS) * len(QUESTIONS)}] "
                f"{model}"
            )

            record = request_model(
                model,
                question_id,
                question,
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
                    "\nОтвет:\n",
                    record["answer"]
                )

            if record["error"]:
                print(
                    "Ошибка:",
                    record["error"]
                )

            if index < (
                    len(MODELS)
                    * len(QUESTIONS)
            ):
                time.sleep(4)

    print(
        "\nЭксперимент завершён."
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
