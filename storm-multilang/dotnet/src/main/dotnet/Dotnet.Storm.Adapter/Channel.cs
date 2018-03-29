﻿using Dotnet.Storm.Adapter.Messaging;
using log4net;
using Newtonsoft.Json;
using System;
using System.Text;

namespace Dotnet.Storm.Adapter
{
    internal sealed class Channel
    {
        private readonly static ILog Logger = LogManager.GetLogger(typeof(Channel));

        public static void Send(OutMessage message)
        {
            Console.WriteLine(message);
            Console.WriteLine("end");
        }

        public static Message Receive<T>() where T : InMessage
        {
            try
            {
                string message = ReadMessage();

                if (message.StartsWith("["))
                {
                    return JsonConvert.DeserializeObject<TaskIdsMessage>(message);
                }
                return JsonConvert.DeserializeObject<T>(message);
            }
            catch (ArgumentNullException ex)
            {
                Logger.Debug($"{ex.Message}");
                throw ex;
            }
            catch (Exception ex)
            {
                //we're expecting this shouldn't happen
                Logger.Error($"Message parsing error: {ex}");
            }

            // just skip incorrect message
            return null;
        }

        private static string ReadMessage()
        {
            StringBuilder message = new StringBuilder();
            string line;
            do
            {
                line = Console.ReadLine();

                if(line == null)
                {
                    throw new ArgumentNullException("Storm is dead.");
                }
                if (line == "end")
                    break;

                if (!string.IsNullOrEmpty(line))
                {
                    message.AppendLine(line);
                }
            }
            while (true);

            return message.ToString();
        }
    }
}
