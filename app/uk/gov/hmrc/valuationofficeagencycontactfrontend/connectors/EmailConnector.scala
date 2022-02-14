/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.valuationofficeagencycontactfrontend.connectors

import play.api.Logging
import play.api.http.Status.{ACCEPTED, OK}
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.requests.DataRequest
import uk.gov.hmrc.valuationofficeagencycontactfrontend.models.{Contact, ContactWithEnMessage}
import uk.gov.hmrc.valuationofficeagencycontactfrontend.utils.DateUtil

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Yuriy Tumakha
 */
@Singleton
class EmailConnector @Inject()(servicesConfig: ServicesConfig,
                               http: HttpClient)
                              (implicit ec: ExecutionContext, dateUtil: DateUtil) extends Logging {

  private val emailServiceBaseUrl = servicesConfig.baseUrl("email")
  private val sendEmailUrl = s"$emailServiceBaseUrl/hmrc/email"
  private val cf_enquiry_confirmation = "cf_enquiry_confirmation"
  private val cf_enquiry_confirmation_cy = "cf_enquiry_confirmation_cy"

  def sendEnquiryConfirmation(contact: Contact)(implicit request: DataRequest[_], messages: Messages, hc: HeaderCarrier): Future[HttpResponse] = {
    val parameters = getParametersJson(contact, request.userAnswers.existingEnquiryCategory.isDefined)

    val templateId = messages.lang.language match {
      case "cy" => cf_enquiry_confirmation_cy
      case _ => cf_enquiry_confirmation
    }
    sendEmail(contact.contact.email, templateId, parameters)
  }

  private def getParametersJson(contact: Contact, isUpdateExistingEnquiry: Boolean)(implicit messages: Messages): JsObject = {
    val submissionDate = dateUtil.nowInUK
    Json.obj(
      "recipientName_FullName" -> contact.contact.fullName,
      "enquirySubject" -> getEnquirySubject(contact, isUpdateExistingEnquiry),
      "submissionDate" -> dateUtil.formattedZonedDate(submissionDate),
      "submissionTime" -> submissionDate.format(dateUtil.timeFormatter),
      "nextStep"       -> getNextStepText(isUpdateExistingEnquiry)
    )
  }

  private def getEnquirySubject(contact: Contact, isUpdateExistingEnquiry: Boolean)(implicit messages: Messages): String = {
    val category = ContactWithEnMessage.enquiryCategory(contact)
    val subCategory = ContactWithEnMessage.enquirySubCategory(contact, isUpdateExistingEnquiry)
    s"$category - $subCategory"
  }

  private def getNextStepText(isUpdateExistingEnquiry: Boolean)(implicit messages: Messages): String =
    if (isUpdateExistingEnquiry) {
      s"${messages("confirmation.existing.p1")}\n${messages("confirmation.existing.p2")}"
    } else {
      messages("confirmation.new.p1")
    }

  private def sendEmail(email: String, templateId: String, parametersJson: JsObject)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val json = Json.obj(
      "to" -> Seq(email),
      "templateId" -> templateId,
      "parameters" -> parametersJson
    )
    val headers = Seq("Content-Type" -> "application/json")

    // The default HttpReads will wrap the response in an exception and make the body inaccessible
    implicit val responseReads: HttpReads[HttpResponse] = (_, _, response: HttpResponse) => response

    http.POST[JsValue, HttpResponse](sendEmailUrl, json, headers).map { res =>
      res.status match {
        case OK | ACCEPTED => logger.info(s"Send email to user successful: ${res.status}")
        case _ => logger.error(s"Send email to user FAILED: ${res.status} ${res.body}")
      }
      res
    }
  }

}
