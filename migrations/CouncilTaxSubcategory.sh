#!/bin/bash

echo "Applying migration CouncilTaxSubcategory"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /councilTaxSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.CouncilTaxSubcategoryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /councilTaxSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.CouncilTaxSubcategoryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCouncilTaxSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.CouncilTaxSubcategoryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCouncilTaxSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.CouncilTaxSubcategoryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "councilTaxSubcategory.title = councilTaxSubcategory" >> ../conf/messages.en
echo "councilTaxSubcategory.heading = councilTaxSubcategory" >> ../conf/messages.en
echo "councilTaxSubcategory.option1 = councilTaxSubcategory" Option 1 >> ../conf/messages.en
echo "councilTaxSubcategory.option2 = councilTaxSubcategory" Option 2 >> ../conf/messages.en
echo "councilTaxSubcategory.checkYourAnswersLabel = councilTaxSubcategory" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def councilTaxSubcategory: Option[String] = cacheMap.getEntry[String](CouncilTaxSubcategoryId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def councilTaxSubcategory: Option[AnswerRow] = userAnswers.councilTaxSubcategory map {";\
     print "    x => AnswerRow(\"councilTaxSubcategory.checkYourAnswersLabel\", s\"councilTaxSubcategory.$x\", true, routes.CouncilTaxSubcategoryController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CouncilTaxSubcategory completed"
