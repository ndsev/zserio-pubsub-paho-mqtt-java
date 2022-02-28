package calculator;

struct I32
{
    int32 value;
};

struct U64
{
    uint64 value;
};

struct Double
{
    float64 value;
};

pubsub CalculatorClient
{
    publish topic("calculator/request") I32 request;

    subscribe topic("calculator/power_of_two") U64 powerOfTwo;
    subscribe topic("calculator/square_root_of") Double squareRootOf;
};

pubsub PowerOfTwoProvider
{
    subscribe topic("calculator/request") I32 request;

    publish topic("calculator/power_of_two") U64 powerOfTwo;
};

pubsub SquareRootOfProvider
{
    subscribe topic("calculator/request") I32 request;

    publish topic("calculator/square_root_of") Double squareRootOf;
};
