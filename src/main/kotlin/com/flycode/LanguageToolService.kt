package com.flycode

import kotlinx.serialization.Serializable
import org.languagetool.JLanguageTool
import org.languagetool.language.AmericanEnglish

object LanguageToolService {
    val langTool = JLanguageTool(AmericanEnglish())

    fun init() {
        langTool.check("Initialize")
    }

    fun check(text: String): CheckResult {
        // comment in to use statistical ngram data:
        // langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
        return CheckResult(
                text,
                (langTool.check(text) ?: emptyList()).map { ruleMatch ->
                    RuleMatchResult(
                            ruleMatch.message,
                            ruleMatch.shortMessage,
                            ruleMatch.suggestedReplacements ?: emptyList(),
                    )
                }
        )
    }

    fun check(texts: List<String>): List<CheckResult> {
        // comment in to use statistical ngram data:
        // langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
        return texts.map { check(it) }
    }
}

@Serializable
data class RuleMatchResult(
        val message: String,
        val shortMessage: String,
        val replacements: List<String>
)

@Serializable data class CheckResult(val text: String, val matches: List<RuleMatchResult>)
