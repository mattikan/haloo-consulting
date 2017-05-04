Feature: BibTex export

  Scenario: BibTex is exported
    Given example reference exists
    And main page is loaded
    When reference with title "Erkin Esimerkkireferenssi" is clicked
    And export bibtex button is clicked
    Then bibtex for article "ESIM1" is visible