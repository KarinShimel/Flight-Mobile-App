using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public interface ITelnetClient
    {
        void Connect();
        void Write(Command command);
        string Read();
        void Disconnect();
        bool IsConnected { get; set; }
    }
}
