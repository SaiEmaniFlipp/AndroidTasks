# AndroidTasks

<p align="center">
  <a href="https://android-arsenal.com/api?level=23"><img alt="API" src="https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat"/></a> 
  <a href="https://github.com/SaiEmaniFlipp/AndroidTasks/actions"><img alt="Build Status" src="https://github.com/semani2/MarvelEpoxy/workflows/Android%20CI/badge.svg"/></a> 
</p>

## Summary
This sample is written in Kotlin and uses the following Architecture Components:
- ViewModel
- LiveData
- Data Binding
- Navigation
- Room
- Hilt 

## App Description
This app helps users create a todo list and then mark them as complete. For sake of simplicity, the app only
relies on a local persistent database.

## Testing
This app was written to work as a template for automated tests for features that use Kotlin Coroutines for threading and Dagger Hilt 
for Dependency Injection. 

Unit tests have been written for the following components:
- ViewModel
- Repository
- DataSource
- Room Dao

UI tests rely on Hilt to provide required test version or `fakes` to simplify testing and enable the tests to run quickly and hermetically.
This is done by creating a `CustomTestRunner` that uses an `Application` configured with Hilt. As per the Hilt testing documentation, 
`@HiltAndroidTest` will automatically create the right Hilt components for each test.

UI Tests have been written to perform page/screen tests and end-to-end integration tests.
