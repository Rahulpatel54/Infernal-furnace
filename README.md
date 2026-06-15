# 🔥 Infernal Furnace — Fabric Mod

A Minecraft Fabric mod for 1.21.1 that adds the **Infernal Furnace** — a netherrack-powered smelting block that burns forever, requires no fuel, and is ignited with Flint & Steel.

---

## ✨ Features

- **Infinite fuel** — once lit, it never stops burning
- **Smelts everything** a vanilla furnace can smelt, at normal speed (200 ticks)
- **Ignite** with Flint & Steel (right-click)
- **Extinguish** with any shovel (right-click)
- Custom textures: cobblestone + netherrack aesthetic
- Uses the vanilla furnace GUI — no custom screen needed
- Light level 13 when lit, 0 when unlit

---

## 🧱 Crafting Recipe

```
C C C
C   C
C N C
```

- `C` = Cobblestone
- `N` = Netherrack (center-bottom)

Result: 1× Infernal Furnace

---

## 🛠️ Setup & Build

### Requirements
- Java 21+
- Minecraft 1.21.1
- Fabric Loader 0.15.11+
- Fabric API 0.100.8+

### Clone & Build

```bash
git clone https://github.com/YourName/infernal_furnace.git
cd infernal_furnace
./gradlew genSources
./gradlew build
```

The built `.jar` will be at `build/libs/infernal_furnace-1.0.0.jar`.

Copy it into your `.minecraft/mods/` folder along with Fabric API.

---

## 📁 Project Structure

```
src/main/
├── java/com/yourname/infernal_furnace/
│   ├── InfernalFurnaceMod.java
│   ├── block/
│   │   └── InfernalFurnaceBlock.java
│   └── block/entity/
│       └── InfernalFurnaceBlockEntity.java
└── resources/
    ├── fabric.mod.json
    ├── assets/infernal_furnace/
    │   ├── blockstates/
    │   ├── lang/
    │   ├── models/block/
    │   ├── models/item/
    │   └── textures/block/
    └── data/infernal_furnace/
        ├── recipes/
        └── tags/blocks/mineable/
```

---

## 🤝 Contributing

Pull requests welcome! If you find a bug or want a feature, open an issue.

---

## 📜 License

MIT — do whatever you want with it, just give credit.
