package solutions

import java.io.File
import java.sql.Date

object HigherFun extends App {
  def uploadForm(saec: SignedAndEncodedComplete)(selectBucketUrl: PublisherConfig => String): String = {
    s"""<form action="${selectBucketUrl(saec.pubConf)}" method="post" enctype="multipart/form-data">
       |  <input type="hidden" name="key" value="${saec.fileName}">
       |  <input type="hidden" name="AWSAccessKeyId" value="${saec.accessKey}">
       |  <input type="hidden" name="acl" value="${saec.acl}">
       |  <input type="hidden" name="policy" value="${saec.encodedPolicy}">
       |  <input type="hidden" name="signature" value="${saec.signedPolicy}">
       |  <input type="hidden" name="Content-Type" value="${saec.contentType}">
       |  Select <code>${saec.fileName}</code> for uploading to S3:
       |  <input name="file" type="file">
       |  <br>
       |  <input type="submit" value="Upload">
       |</form>""".stripMargin
  }

  val file = new File("/etc/passwd")
  val pubConf = PublisherConfig(
    name="blah",
    awsAccountName="blah",
    awsSecretKey="blah",
    awsAccessKey="blah",
    bucketName="bucket1")
  val saec = SignedAndEncodedComplete(
    fileName=file.getAbsolutePath,
    contentLength=file.length,
    accessKey="blahBlah",
    secretKey="blah Blah",
    acl="private",
    pubConf=pubConf,
    encodedPolicy="blah blah",
    signedPolicy="blah blah",
    contentType="blah blah")

  val uf1 = uploadForm(saec)(_.homeworkBucketUrl(file.getName))
  val uf2 = uploadForm(saec)(_.uploadBucketUrl(file.getName))
  println(uf1)
  println(uf2)

  case class PublisherConfig(
    name: String,
    awsAccountName: String,
    awsSecretKey: String,
    awsAccessKey: String,
    bucketName: String,
    created: Date = new Date(System.currentTimeMillis),
    active: Boolean = false,
    id: Option[Long] = None
  ) {
    def uploadBucketUrl(file: String): String = s"http://upload.$bucketName/$file"
    def homeworkBucketUrl(file: String): String = s"http://homework.$bucketName/$file"
  }

  case class SignedAndEncodedComplete(
    fileName: String,
    contentLength: Long,
    accessKey: String,
    secretKey: String,
    acl: String,
    encodedPolicy: String,
    signedPolicy: String,
    pubConf: PublisherConfig,
    contentType: String
  )
}
