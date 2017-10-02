#!/bin/bash

echo "Applying migration BusinessRatesSubcategory"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessRatesSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesSubcategoryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessRatesSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesSubcategoryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessRatesSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesSubcategoryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessRatesSubcategory               uk.gov.hmrc.valuationofficeagencycontactfrontend.controllers.BusinessRatesSubcategoryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessRatesSubcategory.title = businessRatesSubcategory" >> ../conf/messages.en
echo "businessRatesSubcategory.heading = businessRatesSubcategory" >> ../conf/messages.en
echo "businessRatesSubcategory.option1 = businessRatesSubcategory" Option 1 >> ../conf/messages.en
echo "businessRatesSubcategory.option2 = businessRatesSubcategory" Option 2 >> ../conf/messages.en
echo "businessRatesSubcategory.checkYourAnswersLabel = businessRatesSubcategory" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def businessRatesSubcategory: Option[String] = cacheMap.getEntry[String](BusinessRatesSubcategoryId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def businessRatesSubcategory: Option[AnswerRow] = userAnswers.businessRatesSubcategory map {";\
     print "    x => AnswerRow(\"businessRatesSubcategory.checkYourAnswersLabel\", s\"businessRatesSubcategory.$x\", true, routes.BusinessRatesSubcategoryController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/valuationofficeagencycontactfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BusinessRatesSubcategory completed"
