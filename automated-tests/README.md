# Tests Automatisés - Intelligent Financial Assistant

Ce projet contient les tests de bout en bout (E2E) pour l'application frontend, utilisant **Selenium WebDriver** et **Java**.

## Prérequis

1.  **Java JDK 17** ou supérieur installé.
2.  **Maven** installé.
3.  **Google Chrome** installé.
4.  L'application Frontend doit être lancée et accessible sur `http://localhost:4200`.

## Structure du Projet

*   `src/test/java/com/ifa/tests/pages` : Page Objects (Abstraction des pages UI).
*   `src/test/java/com/ifa/tests` : Classes de test (LoginTest, TransactionTest).
*   `src/test/resources/testng.xml` : Configuration de la suite de tests.

## Exécuter les Tests

Pour lancer tous les tests :

```bash
mvn test
```

Pour lancer un test spécifique :

```bash
mvn -Dtest=LoginTest test
```

## Rapports

Après l'exécution, les rapports sont générés dans `target/surefire-reports`.
Ouvrez `target/surefire-reports/index.html` pour voir les résultats détaillés.
