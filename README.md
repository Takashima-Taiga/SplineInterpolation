# B3Zemi2022_sample

## サンプル実行までの手順概要
1. IntelliJのインストール
2. GItのインストール
3. サンプルプロジェクトをクローン
4. IntelliJでプロジェクトを開く
5. IntelliJ内でJava SDKをインストール
6. 実行

## 1. IntelliJのインストール
この演習では統合開発環境としてIntelliJを使用することを推奨します。
https://www.jetbrains.com/idea/ で自分のOSに合ったIntelliJ Communityをインストールしましょう。
インストール時のオプションは、こちらが指定するものはありませんのでお好みでどうぞ。

## 2. GItのインストール
佐賀研究室ではプロジェクトのバージョン管理などをGitやGitHubで行っています。
本配属後に本格的に扱うことになるので、B3ゼミで練習しておきましょう。
https://git-scm.com で自分のOSに合ったGitをインストールしましょう。
インストール時のオプションは、こちらが指定するものはありませんのでお好みでどうぞ。
迷ったらそのままNextで問題ないと思います。
（"Choosing the default editor used by Git"は変えたほうがいいかも）
インストールが終了すると再起動が要求されるので面倒ですが再起動します。

## 3. サンプルプロジェクトをクローン
このリポジトリのページ https://github.com/B3Zemi-2022/B3Zemi2022_sample の右上に"Code"と書かれている緑色のボタンがあるので、ここをクリックして、HTTPSを選択し、URLをコピーします。
サンプルプロジェクトを置きたい任意のディレクトリ（ドキュメントなど）でCLI(*1)を開き、以下のコマンドを実行します。
```
git clone {コピーしたURL}
# 今回の場合は git clone https://github.com/B3Zemi-2022/B3Zemi2022_sample.git
```
任意のディレクトリに"B3Zemi2022_sample"というフォルダが生成されているのを確認してください。

*1 コマンドライン（ターミナル、PowerShell、Git Bashなど）

## 4. IntelliJでプロジェクトを開く
手順1でインストールしたIntelliJを開きます。どこかしらにプロジェクトを開くみたいな項目があると思うので（分からなかったら聞いてください）、"B3Zemi2022_sample"フォルダを選択して開きます。

## 5. IntelliJ内でJava SDKをインストール
IntelliJの上部のメニューからFile->Project Structureへと進みます。SDKを選択する部分をクリックし、+ Add SDK->Download JDKへと進みます。出てきたダイアログでversionを11に、vendorをお好み（おすすめはAmazon CorettoかAzul Zulu）に設定し、Downloadをクリックします。ダウンロードには少し時間がかかります（IntelliJウィンドウ右下でダウンロード状況が見れると思います）。

## 6. 実行
プログラムはIntelliJウィンドウ右上の◁ボタンで実行できると思います。できそうでなければ、IntelliJ左側のProject->B3Zemi2022_sample/src/main/java/.../Main.javaを右クリックで、Run 'Main:main()'をクリックして実行します。クリックで最大3つの点を打てるプログラムが立ち上がれば完了です。お疲れ様でした。
