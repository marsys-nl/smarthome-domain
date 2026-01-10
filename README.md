# Smarthome Domain
Repository for the Domain layer of the Smarthome project, which is a home automation system that allows users to control various smart devices in their home.

# Versioning
This project uses a form of [CalVer](https://calver.org/) for versioning. The version format is `YYYY.MM[.PP][-SNAPSHOT]`.
With `YYYY` being the year, `MM` being the month, `PP` being the patch number (optional), and `-SNAPSHOT` indicating a pre-release version.

# Purpose
The `smarthome-domain` module defines the **shared domain primitives** used across the Smarthome platform.
Its purpose is to provide a **common, type-safe ubiquitous language** that can be reused by multiple bounded contexts (such as the hub backend and mobile clients), without coupling those contexts to each other’s internal models or responsibilities.

This module is intentionally **small, stable, and dependency-free**.

# What belongs in this module
This module contains only fundamental domain concepts that are universally understood across the platform:
- Strongly typed identifiers (e.g. `EntityIdentifier`)
- Value objects (e.g. `Temperature`, `Percentage`)
- Small, immutable domain primitives
- Pure data structures with explicit semantics

These type are shared to ensure:
- Compile-time safety
- Consistency across bounded contexts
- Consistent naming and meaning
- Elimination of primitive obsession (`String`, `Int`, etc.)

# What does NOT belong in this module
To preserve clear boundaries and avoid leaking responsibilities, the following **must not** be included in `smarthome-domain`:

- Aggregates or entities
- Commands, events, or event-sourcing logic
- Business rules or decision-making logic
- Projections or UI-facing models
- Serialization concerns (JSON, Protobuf, DTOs)
- Networking or transport-related models
- Persistence or infrastructure code

In other words: **This module contains no behavior that changes state or enforces business invariants.**

# Design principles
- Explicit over nullable
- Immutable by default
- No framework or platform dependencies
- Kotlin Multiplatform compatible
- Stable API with infrequent changes

# Contributing
To ensure a smooth and productive collaboration, please follow the guidelines as stated in [CONTRIBUTING.md](CONTRIBUTING.md).

# Other

## Pruning local branches

To prune and delete local branches please run `git fetch -p && for branch in $(git branch -vv | grep ': gone]' | awk '{print $1}'); do git branch -D $branch; done` in the project root directory

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.