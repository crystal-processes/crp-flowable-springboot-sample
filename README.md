<!-- TOC -->
* [About](#about)
* [Getting Started](#getting-started)
* [Working with Variables](#working-with-variables)
  * [Version Compatibility Testing](#version-compatibility-testing)
  * [Java Serialization Variables](#java-serialization-variables)
  * [JSON Variables](#json-variables)
  * [JPA Variables](#jpa-variables)
  * [Variable Best Practices](#variable-best-practices)
* [Process Instance Migration](#process-instance-migration)
<!-- TOC -->

# About

This project demonstrates practical patterns and common pitfalls when building **Spring Boot applications with Flowable BPMN**. 

Unlike typical "hello world" samples, this repository focuses on real-world challenges that developers face when working with process variables, data persistence, and model evolution. Each example shows both the problematic approach and the recommended solution.

**Target audience:** Java developers building business applications with Flowable who want to avoid maintenance headaches and architecture issues.

# Getting Started

Start with the basics of integrating Flowable with Spring Boot.

## [Initial Spring Boot and Flowable Setup](docs/01_sample/01-initialApp.md)
Standard Spring Boot + Flowable configuration. Follow this guide to create a working foundation.

## [Hello World Process Model](docs/01_sample/02-helloWorld.md)
Learn how to test BPMN models with assertions in your unit and integration tests.

## [Remote Designer Integration](docs/01_sample/03-designer.md)
Automate BPMN model downloads from a remote Flowable designer directly into your Maven build.

# Working with Variables

Variables are the primary mechanism for passing data through process instances. However, improper use leads to significant maintenance costs, versioning problems, and technical debt.

## Version Compatibility Testing

To validate version compatibility and demonstrate upgrade scenarios, this project has a companion test project: **[crp-sample-upgrade-test](https://github.com/crystal-processes/crp-sample-upgrade-test)**.

Each module in the upgrade test project depends on a different version of `crp-flowable-springboot-sample`. Tests follow this pattern:
1. Generate data on the "current" application version and store it in the database
2. Start the new application version and run tests against the existing data

This approach validates that process instances created with one version can continue running correctly after an upgrade.

[See detailed setup instructions](https://github.com/crystal-processes/crp-sample-upgrade-test?tab=readme-ov-file#prerequisites)

## [Java Serialization Variables](docs/02_variables/01_serializable.md)
Why Java serialization for process variables causes problems and should be avoided.

## [JSON Variables](docs/02_variables/02_json.md)
Common pitfalls when using JSON variables and how to structure them properly.

## [JPA Variables](docs/02_variables/03_jpaVariables.md)
How JPA variables work and the hidden complexity of storing entity references in process instances.

## [Variable Best Practices](docs/02_variables/04_onlyGoodVariable.md)
**Recommended reading.** 

Four practical rules for using variables correctly:
1. **Avoid variables when possible** – Think carefully before creating any process variable
2. **Use transient variables** – When you need a variable, make it transient
3. **Store application data outside the process** – Use data objects or JPA entities, not process variables
4. **Access data through services** – Always use service layer to fetch application data, maintaining proper versioning control

These patterns prevent common issues with data consistency, versioning, and maintenance across Flowable upgrades.

# Process Instance Migration

Version 0.3.1 introduces **automated process instance migration** from JPA-based to service-based variable storage.

## [JPA to Services Migration](docs/03_migration/01-jpa-to-services-migration.md)
Automatically migrate existing process instances between architectural versions. The migration process `A001-migration.bpmn` runs automatically on application startup and transforms process variables from JPA entity references to service-based references, ensuring seamless upgrades between versions 0.3.0 and 0.3.1. Validation tests are available in the [crp-sample-upgrade-test](https://github.com/crystal-processes/crp-sample-upgrade-test) project (release-0.3.0 and release-0.3.1) via the `run_test-phase3.sh` script.
