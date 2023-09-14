import { BrowserRouter, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import PocketMoney from "./pages/PocketMoney";
import Wallet from "./pages/Wallet";
import Home from "./pages/Home";
import ChildReguestMoney from "./pages/ChildReguestMoney";
import MoneySendDone from "./pages/MoneySendDone";
import SendPocketMoney from "./pages/SendPocketMoney";
import SendPocketMoneyMsg from "./pages/SendPocketMoneyMsg";
import { AppLayout } from "./layouts/AppLayout";

function App() {
  return (
    <>
      <AppLayout>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="login" element={<Login />} />
            <Route path="wallet" element={<Wallet />} />
            <Route path="pocketMoney" element={<PocketMoney />} />
            <Route path="childReguestMoney" element={<ChildReguestMoney />} />
            <Route path="moneySendDone" element={<MoneySendDone />} />
            <Route path="sendPocketMoney" element={<SendPocketMoney />} />
            <Route path="sendPocketMoneyMsg" element={<SendPocketMoneyMsg />} />
          </Routes>
        </BrowserRouter>
      </AppLayout>
    </>
  );
}

export default App;
