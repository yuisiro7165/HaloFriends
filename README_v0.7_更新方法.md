# Halo Friends v0.7.0 — 多層3Dヘイロー版

Minecraft Java 1.20.1 / Forge 47.4.28 向け。シロコ、ヒナ、ワカモの3種類を収録。

## v0.6 からの主な改善
v0.6 は「薄い立体」ではあったものの、見た目としては**前後差の少ない薄板**に見えやすい状態でした。
そこで v0.7 では、形状そのものを大きく崩さず、**描画を多層化**して立体感を強めています。

### 新しい見え方
- **後ろ側**：少し大きくて暗い背面レイヤー
- **中央**：本体の3Dメッシュ
- **前側**：少し前に出た薄いハイライト面
- **外側**：独立した発光レイヤー

これにより、正面・斜めから見たときに「浮いている小さな立体物」として読み取りやすくなります。

## キャラ別パラメータ
| キャラ | 横幅 | 高さ | 厚み | 傾き | 高さオフセット |
|---|---:|---:|---:|---:|---:|
| シロコ | 1.05 | 0.78 | 0.060 | 25° | -0.97 |
| ヒナ | 1.32 | 0.86 | 0.072 | 22° | -1.01 |
| ワカモ | 0.98 | 1.00 | 0.066 | 18° | -1.06 |

## 更新方法（GitHubのWeb画面）
1. `HaloFriends_v0.7.0_layered3d_update.zip` を解凍する。
2. GitHub → `HaloFriends` → `Code` → `Add file` → `Upload files` を開く。
3. **ZIPではなく解凍した中身**をフォルダ構造を維持してアップロード（既存ファイルを更新）。
4. `Commit changes` → `Actions` → 最新のビルドへ進む。
5. ビルド成功後、Artifacts欄の `HaloFriends-Forge-1.20.1-v0.7.0-JAR` をクリック。
6. ZIPから `HaloFriends-1.20.1-forge-0.7.0.jar` を取り出し、Minecraft Forge 1.20.1 の `mods` フォルダに入れる。
7. **旧JAR（v0.3〜v0.6）は外す**。

## 変更された主なファイル
- `src/main/java/jp/halofriends/client/HaloClient.java`
- `src/main/java/jp/halofriends/client/HaloGlow.java`
- `src/main/java/jp/halofriends/client/HaloMesh3D.java`
- `build.gradle`
- `.github/workflows/build-jar.yml`
- `src/main/resources/META-INF/mods.toml`

## 確認ポイント
- 正面で見たときに、ヘイロー本体の前後差が少し感じられるか
- 横・斜めで、側面と背面レイヤーが見えてくるか
- 発光だけが強すぎず、本体の立体感を邪魔していないか

## アイテムコマンド
```
/give @s halo_friends:shiroko_halo
/give @s halo_friends:hina_halo
/give @s halo_friends:wakamo_halo
```
