# main.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from dotenv import load_dotenv
import os
from openai import OpenAI
import asyncio

load_dotenv()
API_KEY = os.environ['OPENAI_API_KEY']

client = OpenAI(api_key=API_KEY)

app = FastAPI()

class CommentData(BaseModel):
    content: str

class AnalysisResponse(BaseModel):
    result: str

#THREAD_ID = 'thread_20aymOU9rXrR2YAV2WB4LXOx'
THREAD_ID = 'thread_GnkicjsQ96RmaHVq9kEwV2or'
ASSISTANT_ID = 'asst_Lyt4JFFJQ9Y8YgFqNB3gaGiR'

@app.post("/analyze_comment", response_model=AnalysisResponse)
async def analyze_comment(comment_data: CommentData):

    try:
        # Send message to the thread
        message = client.beta.threads.messages.create(
            thread_id=THREAD_ID,
            role="user",
            content=comment_data.content
        )

        # Run the assistant
        run = client.beta.threads.runs.create(
            thread_id=THREAD_ID,
            assistant_id=ASSISTANT_ID,
            
            instructions = """
            <reporter(기자)>을 통해 기사의 작성자를 알아야해.
            <arc_id(아크 아이디)>을 통해 기사의 ID를 알아야해.
            <title(제목)>을 통해 요청이 어떤 건지 한번에 알아볼 수 있어야 해. 가능하면 1문장으로 만들어줘.
            <reqType(요청유형)>은 다음과 같이 2개가 있어. 내용과 가장 유사한 유형을 선택해줘.
                - [맞춤법]는 기사에서 맞춤법을 지적하는 댓글이야.
                - [내용오류]는 기사 내용의 사실 관계, 통계, 인용, 논리적 오류 등을 지적하는 댓글만 [내용오류]에 포함 되는거야. 댓글의 부족한 점이나 틀린 내용을 찾는게 아니라, 기사 내용을 지적하는 댓글을 찾는거야.("이용 편리하게 여의도에 구치소를 설치하자" 이런 종류의 권유나 "법 위에 군림하고 있는 사람에게 법에 의해서 설치된 기관은 거추장스러운 옷에 불과하다" 이런 종류의 내용은 여기에 포함하면 안돼)

            출력형식(너가 출력해야 할 데이터 형식, json형식으로 출력해줘)
            {
            "result": [
                {
                "reporter": "",
                "arc_id": "aaaaaaaaaaaaaaaaaaaaaa",
                "title": "김민석과 이재명에 대한 댓글 내용 검토 요청",
                "reqType": "[내용오류]",
                "content": "댓글에서 김민석을 '80년대 중반 듣보잡 정청래와 차원이 다른 급'으로, 이재명을 '전과4범 사기꾼'으로 언급하고 있습니다. 이는 확인되지 않은 주장일 수 있으며, 기사 내용과 관련하여 사실 확인이 필요합니다.",
                "uuid": "5fbfde67-3338-4953-bf92"
                },
                {
                "reporter": "",
                "arc_id": "bbbbbbbbbbbbbbbbbbb",
                "title": "이화영의 이름 오기 수정 요청",
                "reqType": "[맞춤법]",
                "content": "본문에서 '뛰어쓰기'이 아니라 '띄어쓰기'으로 맞춤법이 잘못 표기되었다는 지적이 있습니다. 
                "uuid": "ddf8bb33-b8a9-469c-8539"
                },
                {
                "reporter": "",
                "arc_id": "ooooooooooooooooooooooooooo",
                "title": "모친상을 부친상으로 정정 요청",
                "reqType": "[내용오류]",
                "content": "기사 제목에서 '모친상'이라고 언급했으나, 실제로는 '부친상'이라는 지적이 있습니다. 상황의 정확한 확인 후 필요시 제목 및 본문의 수정이 요구됩니다.",
                "uuid": "0bb2b636-d366-443a-9346"
                }
            ]
            }
                """
        )

        # Wait for the run to complete
        while run.status not in ["completed", "failed"]:
            await asyncio.sleep(1)
            run = client.beta.threads.runs.retrieve(thread_id=THREAD_ID, run_id=run.id)

        if run.status == "failed":
            raise HTTPException(status_code=500, detail="Analysis failed")

        # Retrieve the assistant's response
        messages = client.beta.threads.messages.list(thread_id=THREAD_ID)

        # Extract the assistant's last message
        assistant_message = None
        for msg in messages:
            if msg.role == "assistant":
                assistant_message = msg
                break

        if assistant_message is None:
            raise HTTPException(status_code=500, detail="No assistant message found")
        
        #return AnalysisResponse(result=messages.data[0].content[0].text.value)
        return AnalysisResponse(result=assistant_message.content[0].text.value)


    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)