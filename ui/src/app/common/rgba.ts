
export class Rgba {
    r: number;
    g: number;
    b: number;
    a: number;

    static RED: Rgba = new Rgba(255, 0, 0, 255);
    static GREEN: Rgba = new Rgba(0, 255, 0, 255);

    constructor(r: number, g: number, b: number, a: number) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    static fromString(rgba: string) : Rgba {
        if (!rgba) {
            return new Rgba(0, 0, 0, 0);
        }
        const myRegexp = new RegExp("^rgba?\\((\\d+),\\s*(\\d+),\\s*(\\d+)(?:,\\s*(\\d+(?:\\.\\d+)?))?\\)$", "g");
        const matches = myRegexp.exec(rgba);
        return new Rgba(parseFloat(matches[1]), parseFloat(matches[2]), parseFloat(matches[3]), parseFloat(matches[4]));
    }

    toString(): string {
        return `rgba(${this.r}, ${this.g}, ${this.b}, ${this.a})`;
    }

    darken = (value: number): Rgba => new Rgba(
        this.r * (1 - value),
        this.g * (1 - value),
        this.b * (1 - value),
        this.a);

    greyScale = (): Rgba => new Rgba(
        (this.r + this.g + this.b) / 3,
        (this.r + this.g + this.b) / 3,
        (this.r + this.g + this.b) / 3,
        this.a);

    tint = (value: number): Rgba => new Rgba(
        this.r + (value * (255 - this.r)),
        this.g + (value * (255 - this.g)),
        this.b + (value * (255 - this.b)),
        this.a);

    transparency = (newValue: number): Rgba => new Rgba(this.r, this.g, this.b, newValue);

    interpolate = (other: Rgba, percentage: number): Rgba => {
        const colorVal = (prop) => Math.round(this[prop] * (1 - percentage) + other[prop] * percentage);
        return new Rgba(colorVal('r'), colorVal('g'), colorVal('b'), this.a);
    }
}