package com.wolf.travelscout.aws

import com.amazonaws.regions.Regions

object AwsConstants {

    val COGNITO_IDENTITY_ID: String = "us-east-2:d9b70d8e-674e-47c0-9762-36ccc72b1f06"
    val COGNITO_REGION: Regions = Regions.US_EAST_2 // Region
    val BUCKET_NAME: String = "android-demo"

    val S3_URL: String = "https://$BUCKET_NAME.s3.us-east-2.amazonaws.com/"
    val folderPath = "images/"

}