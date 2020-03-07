import itertools
import json

final_display_data = {}
displayData = [{}]
operator_category = {}
operator_json = [{}]

# Add here to add the filtering operators 
filterOperatorList = ["Take(3)", "Filter", "Skip(3)", "Take Last(2)"]
filterOperatorDescription = [ "emits only the first n items emitted by an Observable.",
                              "emit only those items from an Observable that pass a predicate test.",
                              "suppress the first n items emitted by an Observable.",
                              "emits only the last n items emitted by an Observable." ]
filterOperatorLinks = ["http://reactivex.io/documentation/operators/take.html",
                       "http://reactivex.io/documentation/operators/filter.html",
                       "http://reactivex.io/documentation/operators/skip.html",
                       "http://reactivex.io/documentation/operators/takelast.html" ]   

filter_operator_dict = {}

# Creates filter operators json
for operator, description, link in zip(filterOperatorList, filterOperatorDescription, filterOperatorLinks):
    try:
        filter_operator_dict.update({"name": operator})
        filter_operator_dict.update({"description": description})
        filter_operator_dict.update({"link": link})
        operator_json.append(filter_operator_dict)
    except:
        pass
operator_json.pop(0)    
# Creates the filter operator JSON
operator_category.update({"name": "Filtering"})
operator_category.update({"operators": operator_json})
displayData.append(operator_category)

# Add here to add the transforming operators 
transformOperatorList = ["Map (it-> it*2)", "Buffer(2)"]
transformOperatorDescriptions = [ "transforms the items emitted by an Observable by applying a function to each item.",
                              "periodically gather items from an Observable into bundles and emit these bundles rather than emitting the items one at a time."]
transformOperatorLinks = ["http://reactivex.io/documentation/operators/map.html",
                          "http://reactivex.io/documentation/operators/buffer.html"]  

transform_operator_dict = {}
transform_operator_json = [{}]
transform_operator = {}

# Creates transform operator json
for operator, description, link in zip(transformOperatorList, transformOperatorDescriptions, transformOperatorLinks):
    try: 
        transform_operator_dict.update({"name": operator})
        transform_operator_dict.update({"description": description})
        transform_operator_dict.update({"link": link})
        transform_operator_json.append(transform_operator_dict)
    except:
        pass
transform_operator_json.pop(0)
# Creates the json
transform_operator.update({"name": "Transforming"})
transform_operator.update({"operators": transform_operator_json})
displayData.append(transform_operator)

displayData.pop(0)
final_display_data.update({"data": displayData})

#Prints final data
print(json.dumps(final_display_data))
