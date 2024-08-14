from dotenv import load_dotenv
import os
from openai import OpenAI


load_dotenv()
API_KEY = os.environ['OPENAI_API_KEY']
#print(API_KEY)


client = OpenAI(api_key=API_KEY)

#id='asst_Lyt4JFFJQ9Y8YgFqNB3gaGiR'

#id='thread_ofV07fD6UPMPXpFDs7urC4bG'
#thread_20aymOU9rXrR2YAV2WB4LXOx
thread = client.beta.threads.create()
print(thread)

""" 
message = client.beta.threads.messages.create(
  thread_id="thread_ofV07fD6UPMPXpFDs7urC4bG",
  role="assistant",
  content="1+1은 뭐야"
)
print(message) 
"""

#print(assistant)
#print(completion.choices[0].message)
#print(message)