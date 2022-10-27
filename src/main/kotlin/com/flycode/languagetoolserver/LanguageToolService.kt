package com.flycode.languagetoolserver

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.languagetool.JLanguageTool
import org.languagetool.language.AmericanEnglish
import org.languagetool.rules.ITSIssueType
import org.languagetool.rules.RuleMatch

object LanguageToolService {
    private val langTool = JLanguageTool(AmericanEnglish())

    fun init() {
        langTool.check("Initialize")
    }

    /**
     * The main check method. Tokenizes the text into sentences and matches these sentences against
     * all currently active rules.
     *
     * @param text the text to be checked
     * @return a List of {@link RuleMatch} objects
     */
    fun check(text: String): CheckResult {
        // comment in to use statistical ngram data:
        // langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
        return CheckResult(
                text,
                (langTool.check(text) ?: emptyList()).map { ruleMatch ->
                    toMatchRuleResult(ruleMatch)
                }
        )
    }

    fun check(texts: List<String>): List<CheckResult> {
        return texts.map { check(it) }
    }
}

fun toMatchRuleResult(ruleMatch: RuleMatch): RuleMatchResult {
    return RuleMatchResult(
            ruleMatch.message,
            ruleMatch.shortMessage,
            ruleMatch.fromPos,
            ruleMatch.toPos - ruleMatch.fromPos,
            ruleMatch.suggestedReplacements ?: emptyList(),
            toType(ruleMatch.type),
            RuleResult(
                    ruleMatch.rule.id,
                    ruleMatch.rule.subId,
                    ruleMatch.rule.sourceFile,
                    ruleMatch.rule.description,
                    toITSIssueType(ruleMatch.rule.locQualityIssueType),
                    CategoryResult(
                            ruleMatch.rule.category.id.toString(),
                            ruleMatch.rule.category.name
                    )
            )
    )
}

@Serializable
data class RuleMatchResult(
        /**
         * A human-readable explanation describing the error. This may contain one or more
         * corrections marked up with <suggestion>...</suggestion>.
         */
        val message: String,
        /**
         * A shorter human-readable explanation describing the error or an empty string if no such
         * explanation is available.
         */
        val shortMessage: String,
        /**
         * Position of the start of the error (in characters, zero-based, relative to the original
         * input text).
         */
        val offset: Int,
        /** The length of the error */
        val length: Int,
        /**
         * The text fragments which might be an appropriate fix for the problem. One of these
         * fragments can be used to replace the old text
         */
        val replacements: List<String>,
        /** The type of the rule match */
        val type: RuleMatchResult.Type,
        /** The matching rule */
        val rule: RuleResult
) {
    /**
     * Unlike Category, this is specific to a RuleMatch, not to a rule. It is mainly used for
     * selecting the underline color in clients. Note: this is experimental and might change soon
     * (types might be added, deleted or renamed without deprecating them first)
     */
    enum class Type {
        /** Spelling errors, typically red. */
        UnknownWord,

        /** Style errors, typically light blue. */
        Hint,

        /** Other errors (including grammar), typically yellow/orange. */
        Other
    }
}

@Serializable data class CheckResult(val text: String, val matches: List<RuleMatchResult>)

/**
 * Abstract rule class. A Rule describes a language error and can test whether a given pre-analyzed
 * text contains that error using the match(AnalyzedSentence) method. Rules are created whenever a
 * JLanguageTool or a MultiThreadedJLanguageTool object is created. As these objects are not
 * thread-safe, this can happen often. Rules should thus make sure that their initialization works
 * fast. For example, if a rule needs to load data from disk, it should store it in a static
 * variable to make sure the loading happens only once. Rules also need to make sure their match()
 * code is stateless, i.e. that its results are not influenced by previous calls to match() (this is
 * relevant if pipeline caching is used).
 */
@Serializable
data class RuleResult(
        /**
         * A string used to identify the rule in e.g. configuration files. This string is supposed
         * to be unique and to stay the same in all upcoming versions of LanguageTool. It's supposed
         * to contain only the characters A-Z and the underscore.
         */
        val id: String,
        /** Optional, mostly used for XML rules (pulled from there to all rules for uniformity) */
        val subId: String?,
        /**
         * Optional, mostly used for XML rules (pulled from there to all rules for uniformity) For
         * XML rules, this returns the file that this rule was loaded from
         */
        val sourceFile: String?,
        /**
         * A short description of the error this rule can detect, usually in the language of the
         * text that is checked.
         */
        val description: String,
        /** The ITS Issue type */
        val issue: ITSIssueTypeResult,
        val category: CategoryResult
)

@Serializable
data class CategoryResult(
        val id: String,
        val name: String,
)

fun toType(type: RuleMatch.Type?): RuleMatchResult.Type {
    return when (type!!) {
        RuleMatch.Type.UnknownWord -> RuleMatchResult.Type.UnknownWord
        RuleMatch.Type.Hint -> RuleMatchResult.Type.Hint
        RuleMatch.Type.Other -> RuleMatchResult.Type.Other
    }
}

fun toITSIssueType(issue: ITSIssueType?): ITSIssueTypeResult {
    return when (issue!!) {
        ITSIssueType.Terminology -> ITSIssueTypeResult.Terminology
        ITSIssueType.Mistranslation -> ITSIssueTypeResult.Mistranslation
        ITSIssueType.Omission -> ITSIssueTypeResult.Omission
        ITSIssueType.Untranslated -> ITSIssueTypeResult.Untranslated
        ITSIssueType.Addition -> ITSIssueTypeResult.Addition
        ITSIssueType.Duplication -> ITSIssueTypeResult.Duplication
        ITSIssueType.Inconsistency -> ITSIssueTypeResult.Inconsistency
        ITSIssueType.Grammar -> ITSIssueTypeResult.Grammar
        ITSIssueType.Legal -> ITSIssueTypeResult.Legal
        ITSIssueType.Register -> ITSIssueTypeResult.Register
        ITSIssueType.LocaleSpecificContent -> ITSIssueTypeResult.LocaleSpecificContent
        ITSIssueType.LocaleViolation -> ITSIssueTypeResult.LocaleViolation
        ITSIssueType.Style -> ITSIssueTypeResult.Style
        ITSIssueType.Characters -> ITSIssueTypeResult.Characters
        ITSIssueType.Misspelling -> ITSIssueTypeResult.Misspelling
        ITSIssueType.Typographical -> ITSIssueTypeResult.Typographical
        ITSIssueType.Formatting -> ITSIssueTypeResult.Formatting
        ITSIssueType.InconsistentEntities -> ITSIssueTypeResult.InconsistentEntities
        ITSIssueType.Numbers -> ITSIssueTypeResult.Numbers
        ITSIssueType.Markup -> ITSIssueTypeResult.Markup
        ITSIssueType.PatternProblem -> ITSIssueTypeResult.PatternProblem
        ITSIssueType.Whitespace -> ITSIssueTypeResult.Whitespace
        ITSIssueType.Internationalization -> ITSIssueTypeResult.Internationalization
        ITSIssueType.Length -> ITSIssueTypeResult.Length
        ITSIssueType.NonConformance -> ITSIssueTypeResult.NonConformance
        ITSIssueType.Uncategorized -> ITSIssueTypeResult.Uncategorized
        ITSIssueType.Other -> ITSIssueTypeResult.Other
    }
}

/**
 * Some constants for Localization Quality Issue Type from the Internationalization Tag Set (ITS)
 * Version 2.0.
 */
@Serializable
enum class ITSIssueTypeResult {
    @SerialName("terminology") Terminology,
    @SerialName("mistranslation") Mistranslation,
    @SerialName("omission") Omission,
    @SerialName("untranslated") Untranslated,
    @SerialName("addition") Addition,
    @SerialName("duplication") Duplication,
    @SerialName("inconsistency") Inconsistency,
    @SerialName("grammar") Grammar,
    @SerialName("legal") Legal,
    @SerialName("register") Register,
    @SerialName("locale-specific-content") LocaleSpecificContent,
    @SerialName("locale-violation") LocaleViolation,
    @SerialName("style") Style,
    @SerialName("characters") Characters,
    @SerialName("misspelling") Misspelling,
    @SerialName("typographical") Typographical,
    @SerialName("formatting") Formatting,
    @SerialName("inconsistent-entities") InconsistentEntities,
    @SerialName("numbers") Numbers,
    @SerialName("markup") Markup,
    @SerialName("pattern-problem") PatternProblem,
    @SerialName("whitespace") Whitespace,
    @SerialName("internationalization") Internationalization,
    @SerialName("length") Length,
    @SerialName("non-conformance") NonConformance,
    @SerialName("uncategorized") Uncategorized,
    @SerialName("other") Other,
}
