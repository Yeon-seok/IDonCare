import Icon, { ICON_NAME } from "../common/Icon";
import { useNavigate } from "react-router-dom";
import { useRecoilState, useRecoilValue } from "recoil";
import { useEffect } from "react";
import axios from "axios";
import { userToken } from "../../store/common/atoms";
import useComma from './../../hooks/useComma';
import { userBalanace } from "../../store/wallet/atoms";

type CardButtonType = {
  text: string;
  icon: ICON_NAME;
  link: string;
};

const cardButton: CardButtonType[] = [
  {
    link: "/pocketMoney",
    text: "용돈관리",
    icon: "home",
  },
  {
    link: "/wallet/recharge",
    text: "충전",
    icon: "home",
  },
  {
    link: "/transfer/account",
    text: "송금",
    icon: "home",
  },
];

interface CardButtonProps {
  item: CardButtonType;
}

const CardButton: React.FC<CardButtonProps> = ({ item }) => {
  const navigate = useNavigate();

  return (
    <div className="h-10" onClick={() => navigate(item.link)}>
      <div className="px-2">
        <Icon size="small" name={item.icon} />
      </div>
      <div className="w-16">{item.text}</div>
    </div>
  );
};

function WalletCard() {
  const navigate = useNavigate();

  const Token = useRecoilValue(userToken);

  const [balance, setBalance] = useRecoilState(userBalanace);

  useEffect(() => {
    axios
      .get(`http://j9d209.p.ssafy.io:8081/api/virtual/balance`, {
        headers: { Authorization: Token as string },
      })
      .then((res) => {
        console.log(res.data);
        setBalance(res.data.data.balance);
      });
  }, []);

  return (
    <div className="bg-white rounded-2xl overflow-hidden">
      <div className="p-10 text-white h-[140px] [background:linear-gradient(270deg,_#1c51ad_20%,_rgba(28,_81,_173,_0.3))]">
        <div
          className="ml-3"
          onClick={() => {
            navigate("/wallet");
          }}
        >
          <div>잔액</div>
          <div className="text-m">{useComma(balance)} 원</div>
        </div>
        <div className="flex justify-between px-6 py-5 text-center">
          {cardButton.map((item, index) => {
            return <CardButton item={item} key={index} />;
          })}
        </div>
      </div>
    </div>
  );
}

export default WalletCard;