/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.vo.contact.frontend

import play.api.Logging
import play.api.mvc.Call
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.vo.contact.frontend.connectors.AuditingService
import uk.gov.hmrc.vo.contact.frontend.controllers.routes
import uk.gov.hmrc.vo.contact.frontend.identifiers.*
import uk.gov.hmrc.vo.contact.frontend.journey.pages.{EnglandOrWalesPropertyRouter, HousingBenefitAllowancesRouter}
import uk.gov.hmrc.vo.contact.frontend.models.{CheckMode, Mode, NormalMode}
import uk.gov.hmrc.vo.contact.frontend.utils.UserAnswers

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class Navigator @Inject() (
  auditService: AuditingService
)(using ec: ExecutionContext
) extends Logging:

  private val enquiryDateRouting: UserAnswers => Call = answers =>
    answers.enquiryDate match
      case Some("yes")     => routes.ExistingEnquiryCategoryController.onPageLoad
      case Some("no")      => routes.ExpectedUpdateController.onPageLoad()
      case Some("notKnow") => routes.ExistingEnquiryCategoryController.onPageLoad
      case option          =>
        logger.warn(s"Navigation enquiry date reached with unknown option $option by controller")
        throw RuntimeException(s"Navigation for enquiry date reached with unknown option $option by controller")

  private val propertyAddressRouting: UserAnswers => Call = answers =>
    (answers.contactReason, answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match
      case (Some("new_enquiry"), Some("council_tax_property_poor_repair"), _, _) => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_business_uses"), _, _)        => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_area_change"), _, _)          => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_other"), _, _)                => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_annexe"), _, _)               => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_bill"), _, _)                 => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_band_too_high"), _, _)        => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_band_for_new"), _, _)         => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_property_empty"), _, _)       => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_property_split_merge"), _, _) => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), Some("council_tax_property_demolished"), _, _)  => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_change_valuation"), _)  => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_bill"), _)              => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_changes"), _)           => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_from_home"), _)         => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_other"), _)             => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_not_used"), _)          => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_self_catering"), _)     => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_property_empty"), _)    => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_valuation"), _)         => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, Some("business_rates_demolished"), _)        => routes.CheckYourAnswersController.onPageLoad()
      case (_, _, _, _) if answers.enquiryCategory.contains("housing_benefit")   => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, _, Some("submit_new_application"))           => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, _, Some("check_fair_rent_register"))         => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, _, Some("other_request"))                    => routes.CheckYourAnswersController.onPageLoad()
      case (Some("new_enquiry"), _, _, _)                                        => routes.TellUsMoreController.onPageLoad(NormalMode)
      case (Some("more_details"), _, _, _)                                       => routes.WhatElseController.onPageLoad
      case (Some("update_existing"), _, _, _)                                    => routes.AnythingElseTellUsController.onPageLoad
      case (option, _, _, _)                                                     =>
        logger.warn(s"Navigation reached with unknown option $option by controller")
        throw RuntimeException(s"Navigation reached with unknown option $option by controller")

  private val contactReasonRouting: UserAnswers => Call = answers =>
    answers.contactReason match
      case Some("new_enquiry")     => routes.EnquiryCategoryController.onPageLoad(NormalMode)
      case Some("more_details")    => routes.ExistingEnquiryCategoryController.onPageLoad
      case Some("update_existing") => routes.EnquiryDateController.onPageLoad
      case option                  =>
        logger.warn(s"Navigation for contact reason reached with unknown option $option by controller")
        throw RuntimeException(s"Navigation for contact reason reached with unknown option $option by controller")

  private val enquiryRouting: UserAnswers => Call = answers =>
    answers.enquiryCategory match
      case Some("council_tax")               => routes.CouncilTaxSubcategoryController.onPageLoad(NormalMode)
      case Some("business_rates")            => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)
      case Some("housing_benefit")           => routes.JourneyController.onPageLoad(HousingBenefitAllowancesRouter.key)
      case Some("fair_rent")                 => routes.FairRentEnquiryController.onPageLoad
      case Some("valuations_for_tax")        => routes.ValuationForTaxesController.onPageLoad()
      case Some("providing_lettings")        => routes.ProvidingLettingsController.onPageLoad()
      case Some("valuation_for_public_body") => routes.ValuationAdviceController.onPageLoad()
      case Some(option)                      =>
        logger.warn(s"Navigation for enquiry category reached with unknown option $option by controller")
        throw RuntimeException(s"Navigation for enquiry category reached with unknown option $option by controller")
      case None                              =>
        logger.warn("Navigation for enquiry category reached without selection of enquiry by controller")
        throw RuntimeException("Navigation for enquiry category reached without selection of enquiry by controller")

  private val contactDetailsRouting: UserAnswers => Call = answers =>
    (answers.contactReason, answers.enquiryCategory) match
      case (Some("more_details"), _)    => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (Some("update_existing"), _) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("council_tax"))     => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("business_rates"))  => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("housing_benefit")) => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some("fair_rent"))       => routes.PropertyAddressController.onPageLoad(NormalMode)
      case (_, Some(sel))               =>
        logger.warn(s"Navigation for contact details page reached with an unknown selection $sel of enquiry by controller")
        throw RuntimeException(s"Navigation for contact details page reached unknown selection $sel of enquiry by controller")
      case _                            =>
        logger.warn("Navigation for contact details page reached without selection of enquiry by controller")
        throw RuntimeException("Navigation for contact details page reached without selection of enquiry by controller")

  private val confirmationPageRouting: UserAnswers => Call = answers =>
    import answers.*

    contact().fold(
      msg =>
        logger.warn(msg)
        throw RuntimeException(msg)
      ,
      _ =>
        (enquiryCategory.isDefined, existingEnquiryCategory.isDefined) match
          case (true, false) => enquiryRouting(enquiryCategory, routes.ConfirmationController.onPageLoad())
          case (false, true) => enquiryRouting(existingEnquiryCategory, routes.ConfirmationController.onPageLoad())
          case _             =>
            logger.warn("Navigation for confirmation page - Enquiry or Existing Enquiry Subcategory not defined")
            throw RuntimeException("Navigation for confirmation page - Enquiry or Existing Enquiry Subcategory not defined")
    )

  private def enquiryRouting(enquiry: Option[String], confirmationCall: Call): Call =
    val confirmationCategories = Set("council_tax", "business_rates", "housing_benefit", "fair_rent", "other")
    enquiry match
      case Some(category) if confirmationCategories(category) => confirmationCall
      case Some(sel)                                          =>
        logger.warn(s"Navigation for confirmation page reached with an unknown selection $sel of enquiry by controller")
        throw RuntimeException(s"Navigation for confirmation page reached unknown selection $sel of enquiry by controller")
      case None                                               =>
        logger.warn("Navigation for confirmation page reached without selection of enquiry by controller")
        throw RuntimeException("Navigation for confirmation page reached without selection of enquiry by controller")

  private val businessRatesPageRouting: UserAnswers => Call = answers =>
    answers.businessRatesSubcategory match
      case Some("business_rates_changes")          => routes.JourneyController.onPageLoad(EnglandOrWalesPropertyRouter.key)
      case Some("business_rates_self_catering")    => routes.BusinessRatesSelfCateringController.onPageLoad()
      case Some("business_rates_from_home")        => routes.DatePropertyChangedController.onPageLoad()
      case Some("business_rates_change_valuation") => routes.JourneyController.onPageLoad(EnglandOrWalesPropertyRouter.key)
      case Some("business_rates_demolished")       => routes.JourneyController.onPageLoad(EnglandOrWalesPropertyRouter.key)
      case Some("business_rates_valuation")        => routes.BusinessRatesSubcategoryController.onValuationPageLoad
      case Some("business_rates_property_empty")   => routes.PropertyEmptyController.onBusinessRatesPageLoad()
      case Some("business_rates_bill")             => routes.BusinessRatesBillController.onPageLoad()
      case Some("business_rates_not_used")         => routes.BusinessRatesPropertyController.onPageLoad()
      case Some("business_rates_other")            => routes.TellUsMoreController.onPageLoad(NormalMode)
      case Some(_)                                 => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None                                    =>
        logger.warn("Navigation for Business Rates page reached without selection of enquiry by controller ")
        throw RuntimeException("Unknown exception in Business Page Routing")

  private val councilTaxPageRouting: UserAnswers => Call = answers =>
    answers.councilTaxSubcategory match
      case Some("council_tax_band_too_high")        => routes.CouncilTaxBandTooHighController.onPageLoad()
      case Some("council_tax_bill")                 => routes.CouncilTaxBillController.onPageLoad()
      case Some("council_tax_band_for_new")         => routes.CouncilTaxBandForNewController.onPageLoad()
      case Some("council_tax_property_empty")       => routes.PropertyEmptyController.onPageLoad()
      case Some("council_tax_property_poor_repair") => routes.PropertyWindWaterController.onPageLoad()
      case Some("council_tax_property_split_merge") => routes.PropertySplitMergeController.onPageLoad()
      case Some("council_tax_property_demolished")  => routes.PropertyDemolishedController.onPageLoad()
      case Some("council_tax_annexe")               => routes.CouncilTaxAnnexeController.onPageLoad()
      case Some("council_tax_business_uses")        => routes.DatePropertyChangedController.onPageLoad()
      case Some("council_tax_area_change")          => routes.PropertyPermanentChangesController.onPageLoad()
      case Some("council_tax_other")                => routes.TellUsMoreController.onPageLoad(NormalMode)
      case Some(_)                                  => routes.ContactDetailsController.onPageLoad(NormalMode)
      case None                                     =>
        logger.warn("Navigation for Council Tax page reached without selection of enquiry by controller ")
        throw RuntimeException("Unknown exception in Council Tax Routing")

  private val tellUsMoreRouting: UserAnswers => Call = answers =>
    (answers.councilTaxSubcategory, answers.businessRatesSubcategory, answers.fairRentEnquiryEnquiry) match
      case (Some("council_tax_property_poor_repair"), _, _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_business_uses"), _, _)        => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_area_change"), _, _)          => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_other"), _, _)                => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_annexe"), _, _)               => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_bill"), _, _)                 => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_band_too_high"), _, _)        => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_band_for_new"), _, _)         => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_property_empty"), _, _)       => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_property_split_merge"), _, _) => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (Some("council_tax_property_demolished"), _, _)  => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_from_home"), _)         => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_bill"), _)              => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_changes"), _)           => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_change_valuation"), _)  => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_property_empty"), _)    => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_not_used"), _)          => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_self_catering"), _)     => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_valuation"), _)         => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_demolished"), _)        => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, Some("business_rates_other"), _)             => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, _, Some("submit_new_application"))           => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, _, Some("check_fair_rent_register"))         => routes.ContactDetailsController.onPageLoad(NormalMode)
      case (_, _, Some("other_request"))                    => routes.ContactDetailsController.onPageLoad(NormalMode)
      case _                                                => routes.CheckYourAnswersController.onPageLoad()

  private val councilTaxAnnexeRouting: UserAnswers => Call = answers =>
    answers.annexeEnquiry match
      case Some("added")   => routes.CouncilTaxAnnexeController.onSelfContainedEnquiryPageLoad()
      case Some("removed") => routes.CouncilTaxAnnexeController.onRemovedPageLoad()
      case _               =>
        logger.warn("Navigation for annexe without selection of enquiry by controller ")
        throw RuntimeException("Unknown exception for annexe routing")

  private val annexeSelfContainedRouting: UserAnswers => Call = answers =>
    answers.annexeSelfContainedEnquiry match
      case Some("yes") => routes.CouncilTaxAnnexeController.onHaveCookingWashingPageLoad()
      case Some("no")  => routes.CouncilTaxAnnexeController.onNotSelfContainedPageLoad()
      case _           =>
        logger.warn("Navigation for is annexe self contained without selection of enquiry by controller ")
        throw RuntimeException("Unknown exception for is annexe self contained routing")

  private val annexeCookingWashingRouting: UserAnswers => Call = answers =>
    answers.annexeHaveCookingWashing match
      case Some("yes") => routes.CouncilTaxAnnexeController.onSelfContainedPageLoad()
      case Some("no")  => routes.CouncilTaxAnnexeController.onFacilitiesPageLoad()
      case _           =>
        logger.warn("Navigation for is annexe cooking washing without selection of enquiry by controller ")
        throw RuntimeException("Unknown exception for is annexe cooking washing routing")

  private val councilTaxBusinessEnquiryRouting: UserAnswers => Call = answers =>
    (answers.councilTaxBusinessEnquiry, answers.businessRatesSubcategory) match
      case (Some("all_property"), _)                                  => routes.DatePropertyChangedController.onPageLoad()
      case (Some("large_property"), _)                                => routes.DatePropertyChangedController.onPageLoad()
      case (Some("small_property"), Some("business_rates_from_home")) => routes.CouncilTaxBusinessController.onSmallPartUsedBusinessRatesPageLoad()
      case (Some("small_property"), _)                                => routes.CouncilTaxBusinessController.onSmallPartUsedPageLoad
      case _                                                          =>
        logger.warn("Navigation for is council tax business enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for is council tax business enquiry routing")

  private val selfCateringPageRouting: UserAnswers => Call = answers =>
    answers.businessRatesSelfCateringEnquiry match
      case Some("england") => routes.PropertyEnglandAvailableLetsController.onPageLoad
      case Some("wales")   => routes.PropertyWalesAvailableLetsController.onPageLoad
      case _               =>
        logger.warn("Navigation for is business rates self catering enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for is business rates self catering routing")

  private val propertyEnglandAvailableLetsRouting: UserAnswers => Call = answers =>
    answers.propertyEnglandAvailableLetsEnquiry match
      case Some("yes") => routes.PropertyEnglandActualLetsController.onPageLoad
      case Some("no")  => routes.PropertyEnglandLetsNoActionController.onPageLoad
      case _           =>
        logger.warn("Navigation for is business rates property enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for is business rates self catering routing")

  private val propertyEnglandActualLetsRouting: UserAnswers => Call = answers =>
    answers.propertyEnglandActualLetsEnquiry match
      case Some("yes") => routes.BusinessRatesSelfCateringController.onEngLetsPageLoad
      case Some("no")  => routes.PropertyEnglandLetsNoActionController.onPageLoad
      case _           =>
        logger.warn("Navigation for is business rates property enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for is business rates self catering routing")

  private val propertyWalesAvailableLetsRouting: UserAnswers => Call = answers =>
    answers.propertyWalesAvailableLetsEnquiry match
      case Some("yes") => routes.PropertyWalesActualLetsController.onPageLoad
      case Some("no")  => routes.PropertyWalesLetsNoActionController.onPageLoad

      case _ =>
        logger.warn("Navigation for is 140 day lets property enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for 140 day lets routing")

  private val propertyWalesActualLetsRouting: UserAnswers => Call = answers =>
    answers.propertyWalesActualLetsEnquiry match
      case Some("yes") => routes.BusinessRatesSelfCateringController.onWalLetsPageLoad
      case Some("no")  => routes.PropertyWalesLetsNoActionController.onPageLoad

      case _ =>
        logger.warn("Navigation for is 70 day lets property enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for 70 day lets routing")

  private val businessRatesPropertyEnquiryRouting: UserAnswers => Call = answers =>
    answers.businessRatesPropertyEnquiry match
      case Some("england") => routes.BusinessRatesPropertyController.onNonBusinessPageLoad()
      case Some("wales")   => routes.DatePropertyChangedController.onPageLoad()
      case _               =>
        logger.warn("Navigation for is business rates property enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for is business rates self catering routing")

  private val FairRentEnquiryRouting: UserAnswers => Call = answers =>
    answers.fairRentEnquiryEnquiry match
      case Some("submit_new_application")   => routes.FairRentEnquiryController.onFairRentEnquiryNew
      case Some("check_fair_rent_register") => routes.FairRentEnquiryController.onFairRentEnquiryCheck
      case Some("other_request")            => routes.TellUsMoreController.onPageLoad(NormalMode)
      case _                                =>
        logger.warn("Navigation for fair rent enquiry reached without selection of enquiry by controller")
        throw RuntimeException("Unknown exception for fair rent enquiry routing")

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    ContactReasonId                        -> contactReasonRouting,
    EnquiryDateId                          -> enquiryDateRouting,
    ExistingEnquiryCategoryId              -> (_ => routes.RefNumberController.onPageLoad),
    RefNumberId                            -> (_ => routes.ContactDetailsController.onPageLoad(NormalMode)),
    EnquiryCategoryId                      -> enquiryRouting,
    CouncilTaxSubcategoryId                -> councilTaxPageRouting,
    BusinessRatesSubcategoryId             -> businessRatesPageRouting,
    ContactDetailsId                       -> contactDetailsRouting,
    PropertyAddressId                      -> propertyAddressRouting,
    WhatElseId                             -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    TellUsMoreId                           -> tellUsMoreRouting,
    AnythingElseId                         -> (_ => routes.CheckYourAnswersController.onPageLoad()),
    CheckYourAnswersId                     -> confirmationPageRouting,
    BusinessRatesSmartLinksId              -> (_ => routes.BusinessRatesSubcategoryController.onPageLoad(NormalMode)),
    DatePropertyChangedId                  -> (_ => routes.TellUsMoreController.onPageLoad(NormalMode)),
    CouncilTaxAnnexeSelfContainedEnquiryId -> annexeSelfContainedRouting,
    CouncilTaxAnnexeEnquiryId              -> councilTaxAnnexeRouting,
    CouncilTaxAnnexeHaveCookingId          -> annexeCookingWashingRouting,
    CouncilTaxBusinessEnquiryId            -> councilTaxBusinessEnquiryRouting,
    BusinessRatesSelfCateringId            -> selfCateringPageRouting,
    BusinessRatesPropertyEnquiryId         -> businessRatesPropertyEnquiryRouting,
    PropertyEnglandAvailableLetsId         -> propertyEnglandAvailableLetsRouting,
    PropertyEnglandActualLetsId            -> propertyEnglandActualLetsRouting,
    PropertyWalesAvailableLetsId           -> propertyWalesAvailableLetsRouting,
    PropertyWalesActualLetsId              -> propertyWalesActualLetsRouting,
    FairRentEnquiryId                      -> FairRentEnquiryRouting
  )

  def nextPage(id: Identifier, mode: Mode)(using hc: HeaderCarrier): UserAnswers => Call =
    (mode match
      case NormalMode =>
        routeMap.getOrElse(id, (_: UserAnswers) => routes.EnquiryCategoryController.onPageLoad(NormalMode))
      case CheckMode  =>
        (_: UserAnswers) => routes.CheckYourAnswersController.onPageLoad()
    ) andThen auditNextUrl

  private def auditNextUrl(call: Call)(using hc: HeaderCarrier): Call =
    auditService.sendContinueNextPage(call.url)
    call
