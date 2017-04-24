Feature: Reference creation

  Scenario: New reference is created normally
    Given new reference page is loaded
    When reference type is set to Book
    And author is set to "Keijo"
    And title is set to "Tarina Keijosta"
    And id is set to "KTK1"
    And create is clicked
    When main page is loaded
    Then reference with title "Tarina Keijosta" exists
