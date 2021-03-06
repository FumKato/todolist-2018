#Author: fum.kato.asagi@gmail.com
Feature: Can I not register TODO?
  Registration will fail because of my poor inputs

  Scenario: 必須項目が未入力かつ不正な日付入力による登録失敗
    Given 一覧ページを表示する
    And Todoアイテムは登録されていない
    And 登録ページを表示する
    When 説明に"Fuga"と入力する
    And 期限に"Piyo"と入力する
    And 登録ボタンをクリックする
    Then 登録ページが表示される
    And メッセージに"タイトルは必須入力です"と表示される
    And メッセージに"期限はyyyy/mm/ddで入力してください"と表示される

  Scenario Outline: <feature>条件での登録失敗
    Given 一覧ページを表示する
    And Todoアイテムは登録されていない
    And 登録ページを表示する
    When タイトルに"<title>"と入力する
    And 説明に"<detail>"と入力する
    And 期限に"<deadline>"と入力する
    And 登録ボタンをクリックする
    Then 登録ページが表示される
    And メッセージに"<message>"と表示される

    Examples: 
      | feature                         | title | detail | deadline   | message                |
      | 期限が日付の形式になっていない                 | Hoge  |        | Piyoぴよ     | 期限はyyyy/mm/ddで入力してください |
      | 期限にマルチバイト文字を含む                  | Hoge  |        | ２０１８／１/1   | 期限はyyyy/mm/ddで入力してください |
      | 期限にマルチバイト文字を含む かつ 存在しない日付が入力される | Hoge  |        | 2018／１０/32 | 期限はyyyy/mm/ddで入力してください |
      | 存在しない日付が入力される                   | Hoge  |        | 2018/10/32 | 存在しない日付です              |

  Scenario: 期限に入力日よりも前の日付が入力された場合に登録失敗する
    Given 一覧ページを表示する
    And Todoアイテムは登録されていない
    And 登録ページを表示する
    When タイトルに"Hoge"と入力する
    And 期限に現在の日付の2日前を入力する
    And 登録ボタンをクリックする
    Then 登録ページが表示される
    And メッセージに"期限は本日以降で入力してください"と表示される

  Scenario: 期限に入力日よりも前の日付が入力された場合に登録失敗する
    Given 一覧ページを表示する
    And Todoアイテムは登録されていない
    And 登録ページを表示する
    When タイトルに"Hoge"と入力する
    And 期限に現在の日付の1年前を入力する
    And 登録ボタンをクリックする
    Then 登録ページが表示される
    And メッセージに"期限は本日以降で入力してください"と表示される
