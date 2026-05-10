# AI Workspace Rules

## Priority Order

1. **rules/** - Architecture and coding standards (highest priority)
2. **context/** - Project and product knowledge
3. **skills/** - Technical capabilities and expertise
4. **examples/** - Templates and implementation patterns

## Conflict Resolution

If conflict occurs:

- **Architecture rules win** - Clean Architecture and KMP principles are non-negotiable
- **Business rules override generic implementation** - Product-specific requirements take precedence
- **Current project state** - Existing implementation patterns guide decisions

## Available Knowledge

### 📋 Product & Context Knowledge

- **`context/product-overview.md`** - Complete Field Survey & Geo-Tagging application overview
- **`context/docs-index.md`** - Main documentation index with project overview
- **`context/project-structure.md`** - Detailed KMP + Clean Architecture structure
- **`context/migration-guide.md`** - Architecture migration guide
- **`context/documentation-overview.md`** - Documentation structure and purpose

### 🛠️ Technical Skills & Expertise

- **`skills/clean-architecture.md`** - Clean Architecture principles with project-specific implementation
- **`skills/android-compose.md`** - Jetpack Compose best practices and patterns
- **`skills/kmp-development.md`** - Kotlin Multiplatform development skills
- **`skills/kmp-commands.md`** - KMP development commands and workflows
- **`skills/documentation-writing.md`** - Documentation writing standards and practices

### 📏 Development Rules & Standards

- **`rules/architecture.md`** - Comprehensive Clean Architecture and KMP guidelines
- **`rules/coding-style.md`** - Complete coding standards and best practices
- **`rules/ai-behavior.md`** - AI behavior guidelines and constraints

### 📝 Templates & Examples

- **`examples/clean-architecture-examples.md`** - Complete Clean Architecture implementation examples
- **`examples/compose-ui-examples.md`** - Jetpack Compose UI components and screens
- **`examples/documentation-examples.md`** - Documentation templates and examples
- **`examples/kmp-structure-examples.md`** - KMP project structure examples
- **`examples/command-examples.md`** - Command usage examples

### 🎯 Development Prompts

- **`prompts/refactor.md`** - Refactoring guidelines and checklist

## Project Context

### Application Overview

- **Name**: Field Survey & Geo-Tagging Application
- **Architecture**: Kotlin Multiplatform + Clean Architecture
- **UI Framework**: Jetpack Compose with Material 3
- **Platforms**: Android (implemented), iOS (ready)
- **Features**: 200+ survey items, real-time search, status filtering, map integration

### Current Implementation Status

- ✅ **Complete KMP Codebase**: 40+ Kotlin files with Clean Architecture
- ✅ **Production Ready**: Authentication, navigation, state management
- ✅ **Comprehensive Documentation**: Complete technical and user documentation
- ✅ **Modern Architecture**: Single activity, type-safe navigation, use cases

### Key Technical Decisions

- **Clean Architecture**: Strict separation of concerns with use cases
- **KMP Strategy**: Shared business logic, platform-specific UI
- **State Management**: ViewModel + StateFlow + Compose
- **Navigation**: Compose Navigation with centralized management
- **Performance**: LazyColumn for 200+ items, memory efficient

## Usage Guidelines

### For Development

1. **Always reference architecture rules first** - `rules/architecture.md`
2. **Follow coding standards** - `rules/coding-style.md`
3. **Use existing patterns** - Check `examples/` before creating new implementations
4. **Maintain consistency** - Follow established project patterns

### For Documentation

1. **Use templates** - Reference `examples/documentation-examples.md`
2. **Follow writing standards** - `skills/documentation-writing.md`
3. **Keep context updated** - Maintain `context/` files with current state
4. **Document decisions** - Include architecture reasoning

### For Problem Solving

1. **Check existing solutions** - Review `examples/` for similar implementations
2. **Follow Clean Architecture** - Ensure compliance with `rules/architecture.md`
3. **Consider KMP implications** - Think about shared vs platform-specific code
4. **Maintain performance** - Follow established patterns for efficiency

## Quick Reference

### Most Important Files

- **`rules/architecture.md`** - Architecture guidelines (highest priority)
- **`skills/clean-architecture.md`** - Implementation patterns
- **`examples/clean-architecture-examples.md`** - Code examples
- **`context/product-overview.md`** - Product understanding

### Common Workflows

1. **Adding New Feature**: Follow Clean Architecture → Use examples → Update documentation
2. **Refactoring**: Use `prompts/refactor.md` → Follow coding standards → Update examples
3. **Debugging**: Check architecture rules → Review similar examples → Document findings

_Last updated: May 10, 2026_
