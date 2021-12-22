package uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.JsString
import play.api.mvc.Request
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.valuationofficeagencycontactfrontend.identifiers.EnquiryCategoryId
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.UserAnswers

/**
 * @author Yuriy Tumakha
 */
class DataRequestUtilSpec extends AnyFlatSpec with should.Matchers {

  val categories: Seq[String] = Seq("housing_benefit", "fair_rent")

  "isEnquiryCategoryOneOf" should "return true for category 'housing_benefit'" in {
    val userAnswers: UserAnswers = new UserAnswers(CacheMap("",
      Map(EnquiryCategoryId.toString -> JsString("housing_benefit"))))
    implicit val dataRequest: DataRequest[_] = DataRequest(FakeRequest(), "sessionId", userAnswers)

    DataRequestUtil.isEnquiryCategoryOneOf(categories: _*) shouldBe true
  }

  "isEnquiryCategoryOneOf" should "return false for category 'business_rates'" in {
    val userAnswers: UserAnswers = new UserAnswers(CacheMap("",
      Map(EnquiryCategoryId.toString -> JsString("business_rates"))))
    implicit val dataRequest: DataRequest[_] = DataRequest(FakeRequest(), "sessionId", userAnswers)

    DataRequestUtil.isEnquiryCategoryOneOf(categories: _*) shouldBe false
  }

  "isEnquiryCategoryOneOf" should "return false for basic request" in {
    implicit val dataRequest: Request[_] = FakeRequest()

    DataRequestUtil.isEnquiryCategoryOneOf(categories: _*) shouldBe false
  }

}
