# BeeFlagFinder

An Android quiz game where you guess the country a flag belongs to. Built with Kotlin and Jetpack Compose.

## Game modes

- **Random**: a random flag from any country.
- **Continent**: pick one of 6 continents (Africa, Asia, Europe, North America, South America, Oceania) and get random flags from that region only.
- **A-Z**: go through every country in alphabetical order.

On the guess screen you see the flag, type the country name and submit. A banner tells you if you were right, and you can skip or move on to the next flag. Score and streak are tracked per session. Answers are case, accent and punctuation insensitive (e.g. `cote d ivoire` matches "Côte d'Ivoire").

**Clues:** every wrong answer reveals the next clue (region and language, then capital, then first letter and name length). After the 4th mistake you get a "you have run out of clues" message. Clues are generated into `countries.json` by the asset script.

## Architecture

MVVM with Clean Architecture in a single module:

```
domain/        Models (Country, Continent, GameMode), CountryRepository, use cases
data/          AssetCountryDataSource (reads countries.json), CountryRepositoryImpl
presentation/
  components/  Shared game-styled UI (background, buttons, top bar, title)
  home/        HomeScreen
  continent/   ChooseContinentScreen
  guess/       GuessScreen, GuessViewModel, FlagCard, StatsRow, ResultBanner
  navigation/  AppNavHost (Navigation Compose)
```

Dependencies are wired manually through `AppContainer` (no DI framework).

## Flag assets

Flags (197 countries, PNG) and `countries.json` live in `app/src/main/assets/`, so the app works offline. Flag images come from [flagcdn.com](https://flagcdn.com). To regenerate them:

```powershell
powershell -ExecutionPolicy Bypass -File tools\download_flags.ps1
```

## Build

Requires Android Studio with a JDK (the bundled JBR works) and the Android SDK.

```
./gradlew :app:assembleDebug
```

Min SDK 26, target SDK 37.

## Authentication

Sign-up/sign-in (Google, Meta, email) lives in the reusable `:auth` library module built on Firebase. See [auth/README.md](auth/README.md).

