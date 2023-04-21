#include <iostream>
#include <fstream>
#include <nlohmann/json.hpp>
#include <Eigen/Dense>
 
using Eigen::MatrixXi;
using namespace std;
using json = nlohmann::json;

int main()
{
    std::ifstream jsonf("example.json");
    json prog; jsonf >> prog;

    json flips = prog["base"]["flips"];
    json secrets = prog["base"]["secrets"];
    json views = prog["base"]["views"];
    std::cout << 
    "Flips: " << flips.size() << 
    ", Views: " << views.size() << 
    ", Secrets: " << secrets.size() << std::endl;

    struct Rule
    {
        vector <string> head;
        vector <string> body;
    };

    json programjson = prog["program"];
    std::cout << "NumOfRules: " << programjson.size() << std::endl;
    int numOfRules = programjson.size();
    int numOfPropositional = 0; vector<string> herband;
    std::vector<std::vector<string>*> programMatrix;

    for (auto& item : programjson.items())
    {
        Rule currentRule;
        nlohmann::json object = item.value();
        vector <string> headrule; headrule.push_back(object.at("head"));
        currentRule.head = headrule;
        herband.push_back(object.at("head"));
        std::cout << object.at("head") << std::endl;
        ++numOfPropositional;

        currentRule.body = object.at("body");
        numOfPropositional = object.at("body").size() + numOfPropositional;
        std::cout << object.at("body") << std::endl;
        programMatrix.push_back(&currentRule.head);
        programMatrix.push_back(&currentRule.body);
        
        // for (auto& it = currentRule.head.rbegin(); it != currentRule.head.rend(); ++it){
        //     std::cout << it << std::endl;
        // }
    }

    class FlattenVector {
 
public:
    int n;
 
    vector<vector<int>::iterator> iStart;
    vector<vector<int>::iterator> iEnd;
    int currIndex;
 
    FlattenVector(vector<vector<string> >& v)
    {
        n = v.size();
        currIndex = 0;
        iStart.resize(n);
        iEnd.resize(n);
 
        for (int i = 0; i < n; i++) {
            iStart[i] = v[i].begin();
            iEnd[i] = v[i].end();
        }
    }
 
    bool hasNext()
    {
        for (int i = 0; i < n; i++) {
            if (iStart[i] != iEnd[i])
                return true;
        }
        return false;
    }
 
    int next()
    {
        if (iStart[currIndex]
            == iEnd[currIndex]) {
            currIndex++;
            return next();
        }

        else
            return *iStart[currIndex]++;
    }
};


return 0;

}