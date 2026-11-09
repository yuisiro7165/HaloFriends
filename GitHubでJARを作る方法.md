# Halo FriendsのJARをGitHub上で作る方法（PCにJDK不要）

これは**Minecraft用のJARそのものではなく、JARを生成するためのソース一式**です。プロジェクトのビルド実行はまだ未検証です。

## 前提
- **GitHubアカウント**が必要です。
- キャラクターの参考画像をもとに作った素材があるため、**Private（非公開）リポジトリ**で作業してください。ファン作品の再配布や収益化は権利を確認してから判断してください。
- GitHub Actions利用が許可されていること（プラン等によって制限があります）。

## 手順
1. GitHubで **New repository** を押し、名前を `halo-friends` にして **Private** を選び、作成。
2. このZIPを解凍し、フォルダの**中身**（`.github`フォルダ、`src`フォルダ、`build.gradle`、`settings.gradle`等）をリポジトリの直下へアップロード。フォルダの入れ子にならないよう注意。
3. `Actions` タブで **Build Halo Friends 1.20.1 Forge JAR** を選択。
4. `Run workflow` を押す（`main`/`master`へのアップロード後、自動で開始される場合もあります）。
5. ジョブが緑の **Success** になったら、その実行ページ下部の `Artifacts` から `HaloFriends-Forge-1.20.1-v0.3.0-JAR` をダウンロード。
6. ArtifactsのZIPを解凍し、中にある `HaloFriends-1.20.1-forge-0.3.0.jar` をMinecraft 1.20.1/Forgeの `mods` フォルダに配置。
7. Minecraftの起動と3種のアイテムの表示を実際に検証する。

## エラーが出たとき
`Actions` → 失敗した実行 → 赤いステップ → ログ全文をコピーしてChatGPTへ渡してください。原因を調査してソースを修正します。

## ご注意
- この環境はForgeGradleの公式依存ファイルをダウンロードできないため、**JARのビルド成功やゲーム内描画の動作は確認できていません**。
- 成功しても互換性・マルチでの正常動作は別途ゲーム内でテストが必要です。
- 仮にGitHub Actions上でビルド成功したJARも、キャラクターのヘイロー画像の取扱いは権利を確認し、友達との私的利用に限定してください。
- **Forge 1.20.1 / Java 17** 用です。Fabric / NeoForge / その他のMinecraftバージョンには対応しません。
