using Microsoft.VisualBasic.CompilerServices;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public class TelnetClient : ITelnetClient
    {
        private TcpClient client;
        NetworkStream networkStream = null;
        System.IO.StreamWriter writer;
        System.IO.StreamReader reader;
        private bool isConnected = false;

        public TelnetClient()
        {
            try
            {
                Disconnect();
            } catch(Exception e)
            {
                Debug.WriteLine("here");
            }
            Connect();
        }

        public bool IsConnected { get { return isConnected; } set { isConnected = value; } }

        public void Connect()
        {
            var ip = ConfigurationManager.AppSettings["SimIP"];
            int port = Int32.Parse(ConfigurationManager.AppSettings["SimTcpPort"].ToString());
            try
            {
                client = new TcpClient();
                client.ReceiveTimeout = 10000;
                client.Connect(new IPEndPoint(IPAddress.Parse(ip), port));

                networkStream = client.GetStream();

                writer = new System.IO.StreamWriter(networkStream);
                writer.AutoFlush = true;
                reader = new System.IO.StreamReader(networkStream);
                IsConnected = true;
                writer.WriteLine("data\n");
                reader.DiscardBufferedData();
                Debug.WriteLine("");
            }
            catch (Exception e)
            {
                Console.WriteLine("Error..... " + e.StackTrace);
            }
        }

        public void Disconnect()
        {
            if (client == null)
            {
                return;
            }
            client.Close();
            client = null;
            IsConnected = false;
        }

        public string Read()
        {
            return reader.ReadLine();
        }

        public void Write(Command command)
        {
            bool notValid = false;
            Console.WriteLine(command.FromCommandToSetReq());
            writer.WriteLine(command.FromCommandToSetReq()); // set
            
            writer.WriteLine("get /controls/engines/current-engine/throttle\r\n"); // get throttle
            string validateThrottle = Read(); // throttle, rudder, aileron, elevator
            if (Double.Parse(validateThrottle) != command.throttle)
                notValid = true;

            writer.WriteLine("get /controls/flight/rudder\r\n"); // get throttle
            string validateRudder = Read(); // throttle, rudder, aileron, elevator
            if (Double.Parse(validateRudder) != command.rudder)
                notValid = true;

            writer.WriteLine("get /controls/flight/aileron\r\n"); // get throttle
            string validateAileron = Read(); // throttle, rudder, aileron, elevator
            if (Double.Parse(validateAileron) != command.aileron)
                notValid = true;

            writer.WriteLine("get /controls/flight/elevator\r\n"); // get throttle
            string validateElevator = Read(); // throttle, rudder, aileron, elevator
            if (Double.Parse(validateElevator) != command.elevator)
                notValid = true;

            if (notValid)
                throw new Exception();
            else
            {
                Console.WriteLine("data in server checks out");
            }
        }
    }
}
