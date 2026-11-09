# Halo Friends Forge 1.20.1 v0.3.0

シロコ・ヒナ・ワカモの3種類の頭上ヘイローを装備するためのForgeプロジェクトです。
**このZIPはソースです。Minecraftで実行するためのJARは、ビルド後に作成されます。**

## 対応環境
- Minecraft Java Edition **1.20.1**
- **Forge 47.4.28** 推奨（Forge 47.4.x）
- Windowsでのビルド: JDK 17 とインターネット接続
- Fabric、NeoForge、Minecraft 1.21.x にはそのまま使えません。

## JARの作り方（Windows）
1. Java JDK 17 をインストールします（Minecraft本体のJavaとは別でもOK）。
2. このZIPを展開し、`build_on_windows.bat` をダブルクリックします。
3. 初回は公式Forge MavenからMDKをダウンロードしてGradleを実行します。
4. 成功すると `dist\HaloFriends-1.20.1-forge-0.3.0.jar` ができます。
5. そのJARを **Forge 1.20.1で起動するMinecraftのmodsフォルダ** に配置します。
6. 友達のPCとマルチサーバーにも **同じJAR** を入れてください。

JDKの確認：コマンドプロンプトで `java -version` を実行し、**17** になっているか確認。
失敗したら `build.log` と `build_errors.log` の内容を教えてください。

## ゲームでの試用
クリエイティブの「道具と実用品」タブから探すか、コマンドで入手します：
```
/give @s halo_friends:shiroko_halo
/give @s halo_friends:hina_halo
/give @s halo_friends:wakamo_halo
```
右クリックして頭装備として装着し、F5（三人称）で見た目を確認します。

## 注意・制約
- **この環境ではForgeGradleの依存ファイルが取得できず、コンパイル・ゲーム起動・マルチ検証は未実施**。ビルドが成功する保証はありません。
- 他のForge 1.20.1 MODと共存するかも未検証です。
- ヘルメットと同時装備不可。防御力は0です。
- 透明テクスチャを描く平面方式です。立体モデルではありません。
- 暗所でも明るく表示されるFull-brightです。周囲を照らしたり、Bloomを出すものではありません。
- 送られた参照画像を基にした非公式のファン向け試作品。権利を確認するまでは公開・販売しないでください。

## ファイル一覧
- `src/main/java`: MOD本体とクライアント描画
- `src/main/resources/assets/halo_friends/textures/entity`: 3人分の透過テクスチャ
- `src/main/resources/assets/halo_friends/textures/item`: アイコン
- `src/main/resources/META-INF/mods.toml`: MODメタデータ
- `build.gradle`: ForgeGradle設定。**reobfJar** が本番向けリマッピングを行います。
- `build_on_windows.bat` / `build_on_windows.ps1`: ビルドの補助

## コンパイル・エラー対応
`java -version` が21になっている場合、JDK17の`JAVA_HOME`を設定してから実行してください。
ビルド途中にGradleやForgeからエラーが出た場合はエラーログを送ってください。
