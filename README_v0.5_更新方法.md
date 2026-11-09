# Halo Friends v0.5.0 — 薄型シャープ3Dヘイロー (Minecraft Forge 1.20.1)

## 更新点
- シロコ / ヒナ / ワカモのヘイローが**正面・裏面・極薄の側面を持つ3D**として表示される（平面画像のビルボード方式ではない）。
- 厚み: シロコ **0.018**、ヒナ **0.022**、ワカモ **0.020** ブロック（モデル座標の厚さ1.0×描画スケール）。
- 別の半透明テクスチャで、輪郭の外側だけに控えめな光のにじみを描画。
- 角度: シロコ68°、ヒナ65°、ワカモ48°（垂直面からの傾き）。
- 元画像の輪郭形状・色・穴を使い、3人の独自模様を維持。

## GitHubに反映する方法
1. 先に `HaloFriends_v0.5.0_3D_update_only.zip` を解凍。
2. 自分の GitHub リポジトリ `HaloFriends` → `Code` → `Add file` → `Upload files` を開く。
3. ZIPの **中身**（フォルダ構造を維持、`.github` を含む）をドラッグしてアップロード。
4. 既存ファイルは同名の上書きとして反映し、`Commit changes` をクリック。
5. `Actions` → 最新の `Build Halo Friends 1.20.1 Forge JAR` を開き、成功したら `Artifacts` の `HaloFriends-Forge-1.20.1-v0.5.0-JAR` をクリック。
6. ZIPを解凍し、`HaloFriends-1.20.1-forge-0.5.0.jar` を取り出す。
7. Forge 1.20.1 の `mods` へ配置。**旧版0.3/0.4のJARは残さない**。単体テスト用の別プロファイルがおすすめ。

## テスト
1. Minecraft Forge 1.20.1 で起動。3アイテムをコマンドで取得:
```
/give @s halo_friends:shiroko_halo
/give @s halo_friends:hina_halo
/give @s halo_friends:wakamo_halo
```
2. 頭スロットに装備して F5 で前後・斜め・真横から見た薄さを確認。
3. 時間を `/time set night` にして明るく見えるか確認（**実際の周囲のブロックは照らさない**）。
4. 装備解除、プレイヤー移動・しゃがみ、マルチプレイ（サーバー・全クライアントへの導入）を検証。

## 制限事項
- これは **JARではなくソース／更新ファイル**。GitHub Actionsでビルドする。
- この実行環境ではForgeGradle依存ファイルがなく、JARコンパイルと実ゲーム内検証は未実施。GitHub Actions の結果とMinecraft上の動作で検証してほしい。
- 発光はフルブライト＋半透明のにじみ表現で、シェーダーのBloomや動的光源ではない。
- 既存同様、ヘルメットとの同時装備は非対応。
- 画像の権利者の許諾なく、このファンMODを商用販売することは避けてください。
