using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace SocketClientConsole
{
    class Program
    {
        static void Main(string[] args)
        {
            const int PORT = 8889;          //服务器端口
            const string HOST = "127.0.0.1";//服务器端ip地址

            IPAddress ip = IPAddress.Parse(HOST);
            IPEndPoint ipe = new IPEndPoint(ip, PORT);

            Socket clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            clientSocket.Connect(ipe);

            //send message
            byte type = 21;
            byte flag = 32;
            string message = "Hello, meilixueshan!";
            message = "我是中华人民共和国公民";
            byte[] data = messageToBytes(type, flag, message);

            clientSocket.Send(data);


            //receive message
            string recStr = "";
            byte[] recBytes = new byte[4096];
            int bytes = clientSocket.Receive(recBytes, recBytes.Length, 0);
            recStr += Encoding.UTF8.GetString(recBytes, 0, bytes);
            Console.WriteLine(recStr);

            clientSocket.Close();

            Console.ReadLine();
        }

        public static byte[] messageToBytes(byte type, byte flag, string message)
        {
            ByteBuffer byteBuffer = ByteBuffer.Allocate(10240);
            byteBuffer.WriteByte(type);
            byteBuffer.WriteByte(flag);
            byteBuffer.WriteString(message);

            return byteBuffer.ToArray();
        }
    }
}