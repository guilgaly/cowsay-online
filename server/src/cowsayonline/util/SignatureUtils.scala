package cowsayonline.util

import java.nio.charset.StandardCharsets.UTF_8
import scala.util.Try

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignatureUtils {

  def signHmacSHA256(stringToSign: String, secret: String): Try[Array[Byte]] =
    Try {
      val hmacSHA256 = Mac.getInstance("HmacSHA256")
      hmacSHA256.init(new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA256"))
      hmacSHA256.doFinal(stringToSign.getBytes(UTF_8))
    }
}
