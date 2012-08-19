package scalacheck

import org.specs2.{ScalaCheck, SpecificationWithJUnit}
import org.scalacheck.{Arbitrary, Gen}
import net.ripe.commons.ip.Ipv4


class IpResourcesScalaCheckTest extends SpecificationWithJUnit with ScalaCheck {

  implicit val genIpv4Address = for {
    value <- Gen.choose(Ipv4.MINIMUM_VALUE, Ipv4.MAXIMUM_VALUE)
  } yield Ipv4.of(value)
  implicit val arbIpv4Address = Arbitrary(genIpv4Address)

  def is =
    "IPv4 address string formatting" ! prop { (address: Ipv4) => address must_== Ipv4.parse(address.toString) }
  end

  // mvn test -Dtest=scalacheck.IpResourcesScalaCheckTest -Dspecs2.commandline=console


  /*def is =
  "Given an IPv4 address" ^ anIpv4Address^
    "When I print it"   ^ printIpv4^
  end

  object anIpv4Address extends Given[Ipv4] {
    def extract(text: String) = for {
      value <- Gen.choose(Ipv4.MINIMUM_VALUE, Ipv4.MAXIMUM_VALUE)
    } yield new Ipv4(value)
  }*/
}