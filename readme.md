# LanguageTool Server

A JSON HTTP Server for [LanguageTool](https://languagetool.org/) with support for batching.

## API

### Check a single text
```http
POST /check
Content-Type: application/json
```
Body:
```json
{ "text": "string" }
```
Response:
```jsonc
{
  // A human-readable explanation describing the error. This may contain one or more
  // corrections marked up with <suggestion>...</suggestion>.
  "message": "string",
  // A shorter human-readable explanation describing the error or an empty string if no such
  // explanation is available.
  "shortMessage": "string",
  // Position of the start of the error (in characters, zero-based, relative to the original
  // input text).
  // (integer)
  "offset": 42,
  // The length of the error
  // (integer)
  "length": 42,
  // The text fragments which might be an appropriate fix for the problem. One of these
  // fragments can be used to replace the old text
  "replacements": ["string"],
  // The type of the rule match:
  //  - UnknownWord (Spelling errors, typically red)
  //  - Hint (Style errors, typically light blue.)
  //  - Other Other errors (including grammar), typically yellow/orange
  "type": "UnknownWord",
  // The matching rule
  "rule": {
    // A string used to identify the rule in e.g. configuration files. This string is supposed
    // to be unique and to stay the same in all upcoming versions of LanguageTool. It's supposed
    // to contain only the characters A-Z and the underscore.
    "id": "string",
    // Optional, mostly used for XML rules (pulled from there to all rules for uniformity)
    "subId": "string",
    // Optional, mostly used for XML rules (pulled from there to all rules for uniformity) For
    // XML rules, this returns the file that this rule was loaded from
    "sourceFile": "string",
    // A short description of the error this rule can detect, usually in the language of the
    // text that is checked.
    "description": "string",
    // The ITS Issue type
    // - terminology
    // - mistranslation
    // - omission
    // - untranslated
    // - addition
    // - duplication
    // - inconsistency
    // - grammar
    // - legal
    // - register
    // - locale-specific-content
    // - locale-violation
    // - style
    // - characters
    // - misspelling
    // - typographical
    // - formatting
    // - inconsistent-entities
    // - numbers
    // - markup
    // - pattern-problem
    // - whitespace
    // - internationalization
    // - length
    // - non-conformance
    // - uncategorized
    // - other
    "issue": "terminology",
    "category": {
      "id": "string",
      "name": "string"
    }
  }
}
```

### Check multiple texts
```http
POST /bulk-check
Content-Type: application/json
```
Body:
```json
{ "texts": ["string"] }
```
Response:
```jsonc
[
  // An array of results like POST /check for each text in the same order
  {
    // A human-readable explanation describing the error. This may contain one or more
    // corrections marked up with <suggestion>...</suggestion>.
    "message": "string",
    // A shorter human-readable explanation describing the error or an empty string if no such
    // explanation is available.
    "shortMessage": "string",
    // Position of the start of the error (in characters, zero-based, relative to the original
    // input text).
    // (integer)
    "offset": 42,
    // The length of the error
    // (integer)
    "length": 42,
    // The text fragments which might be an appropriate fix for the problem. One of these
    // fragments can be used to replace the old text
    "replacements": ["string"],
    // The type of the rule match:
    //  - UnknownWord (Spelling errors, typically red)
    //  - Hint (Style errors, typically light blue.)
    //  - Other Other errors (including grammar), typically yellow/orange
    "type": "UnknownWord",
    // The matching rule
    "rule": {
      // A string used to identify the rule in e.g. configuration files. This string is supposed
      // to be unique and to stay the same in all upcoming versions of LanguageTool. It's supposed
      // to contain only the characters A-Z and the underscore.
      "id": "string",
      // Optional, mostly used for XML rules (pulled from there to all rules for uniformity)
      "subId": "string",
      // Optional, mostly used for XML rules (pulled from there to all rules for uniformity) For
      // XML rules, this returns the file that this rule was loaded from
      "sourceFile": "string",
      // A short description of the error this rule can detect, usually in the language of the
      // text that is checked.
      "description": "string",
      // The ITS Issue type
      // - terminology
      // - mistranslation
      // - omission
      // - untranslated
      // - addition
      // - duplication
      // - inconsistency
      // - grammar
      // - legal
      // - register
      // - locale-specific-content
      // - locale-violation
      // - style
      // - characters
      // - misspelling
      // - typographical
      // - formatting
      // - inconsistent-entities
      // - numbers
      // - markup
      // - pattern-problem
      // - whitespace
      // - internationalization
      // - length
      // - non-conformance
      // - uncategorized
      // - other
      "issue": "terminology",
      "category": {
        "id": "string",
        "name": "string"
      }
    }
  }
]
```

### Running in Docker

```
docker run -p 8080:8080 ghcr.io/flycode-org/langauge-tool-server
```

### Deployment

Deploy to app engine:

- Make sure to set the correct Google Cloud project before deploying

```
./gradlew appEngineDeploy
```
