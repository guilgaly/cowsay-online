import akka.http.scaladsl.server.Route

package object cowsayonline {
  type RouteProvider = () => Route
}
