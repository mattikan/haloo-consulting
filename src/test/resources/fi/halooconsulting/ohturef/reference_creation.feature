Feature: Reference creation

  Scenario: New book reference is created
    Given new reference page is loaded
    When reference type is set to Book
    And field "author" is set to "Keijo"
    And field "title" is set to "Tarina Keijosta"
    And field "id" is set to "KTK1"
    And field "year" is set to "2017"
    And create is clicked
    When main page is loaded
    Then reference with title "Tarina Keijosta" exists
    And reference with title "Tarina Keijosta" is visible

  Scenario: New inproceedings reference is created
    Given new reference page is loaded
    When reference type is set to Inproceedings
    And field "author" is set to "Keijo"
    And field "title" is set to "Tarina Keijosta 2"
    And field "id" is set to "KTK2"
    And field "booktitle" is set to "Kirjan otsikko"
    And create is clicked
    When main page is loaded
    Then reference with title "Tarina Keijosta 2" exists
    And reference with title "Tarina Keijosta 2" is visible

  Scenario: New article reference is created
    Given new reference page is loaded
    When reference type is set to Article
    And field "author" is set to "Marko"
    And field "title" is set to "Työ mies elämän tarinoita"
    And field "year" is set to "1995"
    And field "publisher" is set to "Miesten vessan seinä"
    And field "address" is set to "Lappi"
    And field "pages" is set to "1"
    And field "journal" is set to "Koppi numero 3"
    And id generation button is clicked
    And create is clicked
    When main page is loaded
    Then reference with title "Työ mies elämän tarinoita" exists
    And reference with title "Työ mies elämän tarinoita" is visible

  Scenario: New book reference is created, id is not entered manually
   Given new reference page is loaded
   When reference type is set to Book
   And field "author" is set to "Keijo"
   And field "title" is set to "Tarina Keijosta"
   And field "year" is set to "2077"
   And id generation button is clicked
   And create is clicked
   When main page is loaded
   Then reference with title "Tarina Keijosta" exists
   And reference with id "Kei77" exists