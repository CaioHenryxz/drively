# Drively — assistente para motoristas

Aplicativo Android em Kotlin para configurar o veículo e metas de trabalho, analisar ofertas e consultar o histórico. O módulo `app` reúne as telas em Jetpack Compose, os cálculos e a integração com o serviço remoto.

## Explorar o código

- [Telas do aplicativo](app/src/main/java/com/drively/app/ui)
- [Cálculos e modelos](app/src/main/java/com/drively/app/core)
- [Leitura de ofertas e sobreposição](app/src/main/java/com/drively/app/service)

## Ambiente de desenvolvimento

Abra a pasta que contém `settings.gradle.kts` no Android Studio e sincronize o Gradle. O módulo usa o SDK 35 para compilação e requer Android 8.0 (API 26) ou posterior. Revise a configuração de acesso ao serviço remoto antes de testar autenticação. A distribuição do aplicativo é feita por pacote Android.
