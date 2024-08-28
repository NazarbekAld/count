import {useEffect, useState} from 'react'
import './App.css'
import {Button, Container, Typography} from "@mui/material";



function App() {
    const [count, setCount] = useState(0);
    const [el, setEl] = useState(<Typography variant={"h4"}>Waiting for server...</Typography>);
    useEffect(() => {
        const websocket = new WebSocket(`wss://${location.hostname}:6001/count_flow`);
        websocket.onopen = () => {
            websocket.addEventListener("message", (e) => {
                const dat = JSON.parse(e.data);
                const theCount = dat["theCount"]
                setCount(theCount)
                console.log(theCount);
                return false
            })
            websocket.onclose = () => setEl(<Typography color={"danger"}>Connection with the server is lost. Please reload the page.</Typography>);
            setEl(<Button variant={"contained"} onClick={() => websocket.send(JSON.stringify({ op: "add" }))}>Click me.</Button>)
        }
    }, [setEl])
    return (
      <Container className={"items-center justify-center flex flex-col"}>
          <Typography variant={"h1"}>Count {count}</Typography>
          {el}
      </Container>
    )
}

export default App
