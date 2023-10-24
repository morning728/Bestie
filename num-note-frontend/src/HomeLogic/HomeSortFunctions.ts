


type Record = {
    description: string, б
    username: string,
    mark: number,
    weight: number,
    height: number,
    moodMark: number,
    steps: number,
    sheets: number,
    income: number,
    created: string,
    updated: string,
    status: string,
    id: bigint
};



const sortByDescription = (records) => {
    const sorted = records.sort((a: Record, b: Record) => {
        if (a.description > b.description) {
            return 1;
        }
        else if (a.description < b.description) {
            return -1;
        }
        return 0;
    });
    return Array.from(sorted);
};

const TESTTEST = (records) => {
    return [{
        'created': "2023-10-23T20:06:06.427+00:00",
        'description': "b",
        'username': "test",
        'mark': 0,
        'weight': 0,
        'height': 0,
        'moodMark': 0,
        'steps': 0,
        'sheets': 0,
        'income': 0,
        'updated': "2023-10-23T20:06:06.427+00:00",
        'status': "ACTIVE",
        'id': 13,
    },
    {
        'created': "2023-10-23T20:06:06.427+00:00",
        'description': "a",
        'username': "test",
        'mark': 0,
        'weight': 0,
        'height': 0,
        'moodMark': 0,
        'steps': 0,
        'sheets': 0,
        'income': 0,
        'updated': "2023-10-23T20:06:06.427+00:00",
        'status': "ACTIVE",
        'id': 13,
    },].sort((a, b) => {
        if (a.description > b.description) {
            return 1;
        }
        else if (a.description < b.description) {
            return -1;
        }
        return 0;
    });
};

const sortByDescriptionReversed = (records: Record[]): Record[] => {
    return Array.from(records.sort((a, b) => {
        if (a.description > b.description) {
            return -1;
        }
        else if (a.description < b.description) {
            return 1;
        }
        return 0;
    }));
};

const sortByDate = (records) => {
    console.log(records);
    return records.sort((a, b) => {
        if (a.created > b.created) {
            return 1;
        }
        else if (a.created < b.created) {
            return -1;
        }
        return 0;
    });
};

const sortByDateReversed = (records: Record[]): Record[] => {
    console.log(records);
    return records.sort((a, b) => {
        if (a.created > b.created) {
            return -1;
        }
        else if (a.created < b.created) {
            return 1;
        }
        return 0;
    });
};





export { TESTTEST, sortByDescription, sortByDescriptionReversed, sortByDate, sortByDateReversed };

