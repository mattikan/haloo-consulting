Feature: Reference deletion
  
  Scenario: Reference can be deleted
    Given example reference exists
    And main page is loaded
    When delete for title "ESIM1" is clicked
    And page is reloaded
    Then reference with title "Erkin Esimerkkireferenssi" is not visible