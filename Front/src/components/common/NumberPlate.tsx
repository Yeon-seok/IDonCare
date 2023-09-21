import React from "react";

type ButtonText = string;

interface NumberPlateProps {
  bottomLeftText?: ButtonText | null;
  bottomRightText?: ButtonText | null;
  onNumberClick?: ((num: number | string) => void) | null;
  onLeft?: (() => void) | null;
  onRight?: (() => void) | null;
}

const NumberPlate: React.FC<NumberPlateProps> = ({
  bottomLeftText = null,
  bottomRightText = null,
  onNumberClick = null,
  onLeft = null,
  onRight = null,
}) => {
  const numbers = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9],
    [bottomLeftText || "CLR", 0, bottomRightText || "<-"],
  ];

  return (
    <div className="grid grid-cols-3 gap-6 mb-10 mt-5">
      {numbers.map((row, i) =>
        row.map((num, j) => (
          <button
            key={i * 3 + j}
            className="p-2 text-l"
            onClick={() => {
              if (typeof num === "number" && onNumberClick) {
                onNumberClick(num);
              } else if (num === bottomLeftText && onLeft) {
                onLeft();
              } else if (num === "<-" && onRight) {
                onRight();
              }
            }}
          >
            {num}
          </button>
        ))
      )}
    </div>
  );
};

export default NumberPlate;