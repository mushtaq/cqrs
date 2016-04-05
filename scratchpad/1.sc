import java.net._

val address = new InetSocketAddress("127.0.0.6", 8000)

val socket = new ServerSocket()
val socket2 = new ServerSocket()

InetAddress.getAllByName("localhost")

socket.bind(address)
socket2.bind(new InetSocketAddress("127.0.0.7", 8000))










