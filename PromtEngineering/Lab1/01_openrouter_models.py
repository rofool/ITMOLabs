import getpass
import json
import os
import warnings
from datetime import datetime, timezone
from decimal import Decimal, InvalidOperation
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

BASE_URL = "https://openrouter.ai/api/v1"


def api_get(endpoint, api_key):
    request = Request(
        BASE_URL + endpoint,
        headers={"Authorization": f"Bearer {api_key}", "Accept": "application/json"},
    )
    with urlopen(request, timeout=60) as response:
        return json.load(response)


def is_free_text_model(model):
    pricing = model.get("pricing", {})
    outputs = model.get("architecture", {}).get("output_modalities", [])
    try:
        zero_price = all(
            Decimal(str(pricing.get(field, "NaN"))) == 0
            for field in ("prompt", "completion")
        )
    except InvalidOperation:
        return False

    return model.get("id", "").endswith(":free") and zero_price and "text" in outputs


def main():
    warnings.simplefilter("error", getpass.GetPassWarning)
    api_key = os.environ.get("OPENROUTER_API_KEY") or getpass.getpass(
        "Вставьте ключ OpenRouter и нажмите Enter (ввод скрыт): "
    )
    api_key = api_key.strip()
    if not api_key:
        raise SystemExit("Ключ не введён.")

    key_info = api_get("/key", api_key)["data"]
    print("Ключ принят сервисом.")
    quota = key_info.get("free_model_daily_requests")
    if quota:
        print("Остаток бесплатных запросов на день:", quota.get("remaining", "не указан"))

    catalog = api_get("/models", api_key)
    models = catalog["data"]
    free_models = sorted(
        (model for model in models if is_free_text_model(model)),
        key=lambda model: model["id"],
    )

    now = datetime.now(timezone.utc)
    result_dir = Path(__file__).resolve().parent / "results" / now.strftime("%Y%m%dT%H%M%S%fZ")
    result_dir.mkdir(parents=True, exist_ok=True)
    snapshot = {"retrieved_at_utc": now.isoformat(), "base_url": BASE_URL, **catalog}
    (result_dir / "available_models.json").write_text(
        json.dumps(snapshot, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    lines = [f"{model['id']} | {model.get('name', '')}" for model in free_models]
    (result_dir / "free_models.txt").write_text("\n".join(lines), encoding="utf-8")
    print(f"\nВсего моделей в каталоге: {len(models)}")
    print(f"Бесплатных текстовых моделей-кандидатов: {len(free_models)}\n")
    print("\n".join(lines) or "Подходящие модели не найдены.")
    print(f"\nРезультаты сохранены: {result_dir}")
    print("Генерация ещё не запускалась. Доступность моделей проверим запросами.")


if __name__ == "__main__":
    try:
        main()
    except HTTPError as error:
        hints = {
            401: "Проверьте полный ключ и его срок действия.",
            403: "Сервис отказал в доступе. Проверьте ограничения аккаунта.",
            429: "Достигнут лимит запросов. Повторите позже.",
        }
        raise SystemExit(f"HTTP {error.code}. " + hints.get(error.code, "Ошибка сервиса.")) from None
    except (URLError, TimeoutError):
        raise SystemExit("Не удалось связаться с OpenRouter. Проверьте интернет.") from None
    except getpass.GetPassWarning:
        raise SystemExit("Запустите скрипт во вкладке Terminal: здесь нельзя скрыть ввод.") from None
    except (KeyError, ValueError):
        raise SystemExit("Сервис вернул неожиданный формат данных. Сообщите об ошибке.") from None
    except KeyboardInterrupt:
        raise SystemExit("\nЗапуск отменён.") from None
