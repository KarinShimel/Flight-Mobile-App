using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using FlightMobileApp.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using FlightMobileApp;
using System.Net;
using System.Net.Http;
using System.Diagnostics;
using System.Configuration;
using System.Drawing;
using System.IO;
using Microsoft.AspNetCore.Mvc.Formatters;
using System.Net.Http.Headers;
namespace FlightMobileApp.Controllers
{

    public class ScreenShotsController
    {
        [Route("screenshot")]
        public async Task<System.IO.Stream> Screenshots()
        {
            var ip = ConfigurationManager.AppSettings["SimIP"];
            string port = ConfigurationManager.AppSettings["SimHttpPort"];
            HttpClient client = new HttpClient();
            try
            {
                string msg = "http://" + ip + ":" + port + "/screenshot";
                HttpResponseMessage response = await client.GetAsync(msg);
                response.EnsureSuccessStatusCode();
                System.IO.Stream responseBody = await response.Content.ReadAsStreamAsync();
                if (response.IsSuccessStatusCode)
                {
                    return responseBody;
                }
                else
                    Debug.WriteLine("\nError in getting flights from server");
                return null;
            }
            catch (HttpRequestException e)
            {
                Console.WriteLine("Message :{0} ", e.Message);
            }
            client.Dispose();
            return null;
        }
    }

    [Route("api/[controller]")]
    [ApiController]
    public class CommandController : ControllerBase
    {
        private ITelnetClient client;
        public CommandController(ITelnetClient telnetClient)
        {
            client = telnetClient;
        }
        

            // POST: api/Command
            [HttpPost]
        [ProducesResponseType(StatusCodes.Status421MisdirectedRequest)]
        public ActionResult Post([FromBody] Command value)
        {
            try
            {
                client.Write(value);
                return Ok();
            }
            catch (Exception e)
            {
                return StatusCode(421);
            }
        }
    }
}
