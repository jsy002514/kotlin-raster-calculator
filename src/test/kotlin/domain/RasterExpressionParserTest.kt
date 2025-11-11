package domain

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import kotlin.math.exp

class RasterExpressionParserTest {
    private val parser = RasterExpressionParser()

    @Test
    fun `간단한_덧셈_수식을_후위표기법으로_변환한다`(){
        val expression = "b03 + b04"
        val postfix = parser.parseToPostfix(expression)
        assertThat (postfix).containsExactly("b03","b04","+")
    }

    @Test
    fun `괄호가_포함된_수식을_후위표기법으로_변환한다`(){
        val expression = "(b03 + b04) / 2"
        val postfix = parser.parseToPostfix(expression)
        assertThat(postfix).containsExactly("b03","b04","+","2","/")
    }

    @Test
    fun `사용자_비전_수식을_후위표기법으로_변환한다`(){
        val expression = "( b03 + b04 ) / ( b03 - b04 )"
        val postfix = parser.parseToPostfix(expression)
        assertThat(postfix).containsExactly("b03", "b04", "+", "b03", "b04", "-", "/")
    }
}
