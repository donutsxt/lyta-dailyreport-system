#!/bin/bash

# Spring Bootアプリケーションの起動
#jarファイルにしたSpring Bootアプリケーションを起動するためのコマンドです。最後の & は、バックグラウンドでJava（Tomcat）を実行するための指定です。& をつけない場合、次のApache 2の起動コマンドを実行できません。
java -jar /var/www/java/DailyReportSystemApplication-0.0.1-SNAPSHOT.jar &

# Apache2の起動
#/var/run/apache2/apache2.pidは、Apache 2のプロセスIDを管理するファイルです。このファイルが残っていると、コンテナを終了して再度起動した際にApache 2が動作しなくなるため、コンテナ起動時に強制削除しています。
rm -f /var/run/apache2/apache2.pid
#Apache 2を実行します。-D FOREGROUNDは、Apache 2をフォアグラウンドで実行するためのコマンドです。Apache 2もバックグラウンドで実行すると、コンテナがすぐ終了してしまうため、このような対処をします。
apachectl -D FOREGROUND