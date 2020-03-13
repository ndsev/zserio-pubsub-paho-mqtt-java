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
    publish("calculator/request") I32 request;

    subscribe("calculator/power_of_two") U64 powerOfTwo;
    subscribe("calculator/square_root_of") Double squareRootOf;
};

pubsub PowerOfTwoProvider
{
    subscribe("calculator/request") I32 request;

    publish("calculator/power_of_two") U64 powerOfTwo;
};

pubsub SquareRootOfProvider
{
    subscribe("calculator/request") I32 request;

    publish("calculator/square_root_of") Double squareRootOf;
};
