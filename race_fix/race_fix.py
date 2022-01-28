import json
import glob
import pathlib

path = pathlib.Path().resolve()
all_files = glob.glob(str(path) + "/*.json")

for file in all_files:
    with open(file, 'r+') as f:
        data = json.load(f)
        if data["start"]["location"]["world"] == "world":
            data["start"]["location"]["y"] = data["start"]["location"]["y"] - 64
            data["finish"]["location"]["y"] = data["start"]["location"]["y"] - 64
            for cp in data["checkpoints"]:
                cp["location"]["y"] = cp["location"]["y"] - 64
            f.seek(0) 
            json.dump(data, f, indent=0)
            f.truncate()
        else:
            f.close()
            continue
    f = open(file)
    temp = f.read().replace("\n","").replace(" ","")
    f.close()
    f = open(file,"w")
    f.write(temp)
    f.close()