using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileApp.Models
{
    public class Command
    {
        public double aileron { get; set; }
        public double rudder { get; set; }
        public double elevator { get; set; }
        public double throttle { get; set; }
        public string FromCommandToSetReq()
        {
            return "set /controls/engines/current-engine/throttle " + this.throttle.ToString() + "\r\n" +
                "set /controls/flight/rudder " + this.rudder.ToString() + "\r\n" +
                "set /controls/flight/aileron " + this.aileron.ToString() + "\r\n" +
                "set /controls/flight/elevator " +this.elevator.ToString() + "\r\n";
        }
       
    }
}
