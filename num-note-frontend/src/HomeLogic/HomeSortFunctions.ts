


type Record = {
    description: string, 
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


const sortByDescriptionReversed = (records) => {
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
    return Array.from(records.sort((a, b) => {
        if (a.created > b.created) {
            return 1;
        }
        else if (a.created < b.created) {
            return -1;
        }
        return 0;
    }));
};

const sortByDateReversed = (records) => {
    return Array.from(records.sort((a, b) => {
        if (a.created > b.created) {
            return -1;
        }
        else if (a.created < b.created) {
            return 1;
        }
        return 0;
    }));
};

const sortByMark = (records) => {
    records = sortByDateReversed(records);
    return Array.from(records.sort((a, b) => {
        if (a.mark > b.mark) {
            return -1;
        }
        else if (a.mark < b.mark) {
            return 1;
        }
        return 0;
    }));
};





export {sortByMark, sortByDescription, sortByDescriptionReversed, sortByDate, sortByDateReversed };

